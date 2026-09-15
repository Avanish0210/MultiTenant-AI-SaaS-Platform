# 🚀 Build a React Frontend for ProjectAI — AI-Powered Code Generation Platform

## 📋 PROJECT OVERVIEW

**ProjectAI** is an AI-powered collaborative code generation platform where users can:
- Create projects (React/Vite/Tailwind/DaisyUI starter templates)
- Chat with an AI that reads/writes files in their project in real-time (streaming SSE)
- Collaborate with team members on projects with role-based permissions
- Manage subscriptions via Stripe (free tier + paid plans)
- Preview deployed projects

**Tech Stack (Backend):** Spring Boot 4 + Java 21, PostgreSQL, MinIO (object storage), Stripe payments, Spring AI (OpenAI/Gemini), JWT Auth, MapStruct, Lombok

---

## 🏗️ ARCHITECTURE & DATA MODEL

### Database Entities

#### 1. User
```
- id: Long (auto-generated)
- username: String (unique, used as email)
- password: String (BCrypt encoded)
- name: String
- stripeCustomerId: String (unique, nullable — set after first Stripe checkout)
- createdAt: Instant
- updatedAt: Instant
- deletedAt: Instant (soft delete, nullable)
```
Implements `UserDetails` (Spring Security). No roles/authorities — permissions are project-scoped via `ProjectMember`.

#### 2. Project
```
- id: Long (auto-generated)
- name: String (required)
- isPublic: Boolean (default false)
- createdAt: Instant
- updatedAt: Instant
- deletedAt: Instant (soft delete)
```
Access is controlled through `ProjectMember` — a user can only see projects where they are a member.

#### 3. ProjectMember (Composite PK: projectId + userId)
```
- id: ProjectMemberId (composite: projectId, userId)
- project: Project (ManyToOne)
- user: User (ManyToOne)
- projectRole: ProjectRole (enum: OWNER, EDITOR, VIEWER)
- invitedAt: Instant
- acceptedAt: Instant
```

#### 4. ProjectFile
```
- id: Long (auto-generated)
- project: Project (ManyToOne, required)
- path: String (e.g., "src/App.tsx", "package.json")
- minioObjectKey: String (e.g., "42/src/App.tsx" — stored in MinIO)
- createdAt: Instant
- updatedAt: Instant
```
Represents metadata for files stored in MinIO. The actual file content is in MinIO, keyed by `{projectId}/{filePath}`.

#### 5. ChatSession (Composite PK: projectId + userId)
```
- id: ChatSessionId (composite: projectId, userId)
- project: Project
- user: User
- createdAt: Instant
- updatedAt: Instant
- deletedAt: Instant
```
Each user gets one chat session per project.

#### 6. ChatMessage
```
- id: Long (auto-generated)
- chatSession: ChatSession (ManyToOne, FK: projectId + userId)
- role: MessageRole (enum: USER, ASSISTANT, SYSTEM, TOOL)
- content: String (text)
- tokenUsed: Integer (default 0)
- createdAt: Instant
```

#### 7. ChatEvent
```
- id: int (auto-generated)
- chatMessage: ChatMessage (ManyToOne)
- type: ChatEventType (enum: THOUGHT, MESSAGE, FILE_EDIT, TOOL_LOG)
- sequenceOrder: Integer (ordering within a message)
- content: String (text)
- filePath: String (nullable, for FILE_EDIT events)
- metadata: String (JSON, for TOOL_LOG events — contains tool args)
```
Each assistant response is parsed into multiple events: a THOUGHT event (thinking time), MESSAGE events (markdown text), FILE_EDIT events (generated file content), and TOOL_LOG events (file reads).

#### 8. Plan
```
- id: Long (auto-generated)
- name: String (e.g., "Free", "Pro", "Enterprise")
- stripePriceId: String (unique — Stripe price ID)
- maxProduct: Integer
- maxTokensPerDay: Integer
- maxPreviews: Integer
- unlimitedAi: Boolean
- active: Boolean
```

#### 9. Subscription
```
- id: Long (auto-generated)
- user: User (ManyToOne)
- plan: Plan (ManyToOne)
- status: SubscriptionStatus (enum: ACTIVE, TRIALING, CANCELED, PAST_DUE, INCOMPLETE)
- stripeSubscriptionId: String
- currentPeriodStart: Instant
- currentPeriodEnd: Instant
- cancelAtPeriodEnd: Boolean (default false)
- createdAt: Instant
- updatedAt: Instant
```

#### 10. Preview (NOT an entity — not JPA annotated, likely a future feature)
```
- id: Long
- projectId: Long
- namespace: String
- podName: String
- previewUrl: String
- status: String
- startedAt: Instant
- terminatedAt: Instant
- createdAt: Instant
```

#### 11. UsageLog (NOT an entity — not JPA annotated)
```
- id: Long
- user: User
- project: Project
- action: String
- tokensUsed: Integer
- durationMs: Integer
- metaData: String (JSON: model_used, prompt_used)
- createdAt: Instant
```

### Enums

| Enum | Values |
|------|--------|
| `ProjectRole` | `OWNER` (VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBER), `EDITOR` (VIEW, EDIT, DELETE, VIEW_MEMBER), `VIEWER` (VIEW, VIEW_MEMBER) |
| `ProjectPermission` | `VIEW` ("project:view"), `EDIT` ("project:edit"), `DELETE` ("project:delete"), `MANAGE_MEMBERS` ("project_members:manage"), `VIEW_MEMBER` ("project_members:view") |
| `MessageRole` | `USER`, `ASSISTANT`, `SYSTEM`, `TOOL` |
| `ChatEventType` | `THOUGHT`, `MESSAGE`, `FILE_EDIT`, `TOOL_LOG` |
| `SubscriptionStatus` | `ACTIVE`, `TRIALING`, `CANCELED`, `PAST_DUE`, `INCOMPLETE` |
| `PreviewStatus` | `CREATING`, `RUNNING`, `FAILED`, `TERMINATED` |

---

## 🔐 AUTHENTICATION

- **Type:** JWT Bearer token
- **Header:** `Authorization: Bearer <token>`
- **Token expiry:** 10 minutes
- **Token payload:** `sub` = username, `userId` = user ID
- **Public routes:** `POST /api/auth/signup`, `POST /api/auth/login`, `POST /webhooks/payment`
- **All other routes require authentication**
- **Session:** Stateless (no cookies)

---

## 🌐 API ENDPOINTS

### 1. Auth — `POST /api/auth/*`

| Method | Endpoint | Body | Response | Description |
|--------|----------|------|----------|-------------|
| POST | `/api/auth/signup` | `{ username: string (email), name: string, password: string (min 4 chars) }` | `{ token: string, user: { id, username, name } }` | Register new user |
| POST | `/api/auth/login` | `{ username: string (email), password: string (min 4, max 58) }` | `{ token: string, user: { id, username, name } }` | Login |
| GET | `/api/auth/me` | — | `{ id, username, name }` | Get current user profile |

### 2. Projects — `GET/POST /api/projects/*`

| Method | Endpoint | Body/Params | Response | Description |
|--------|----------|-------------|----------|-------------|
| GET | `/api/projects` | — | `[{ id, projectName, createdAt, updatedAt }]` | List all projects the user has access to |
| GET | `/api/projects/{id}` | — | `{ id, name, createdAt, updatedAt, owner: { id, username, name } }` | Get project details |
| POST | `/api/projects` | `{ name: string }` | `{ id, name, createdAt, updatedAt, owner }` | Create a new project (auto-initializes from React template) |
| PATCH | `/api/projects/{id}` | `{ name: string }` | `{ id, name, createdAt, updatedAt, owner }` | Update project name |
| DELETE | `/api/projects/{id}` | — | `204 No Content` | Soft-delete project |

### 3. Project Members — `/api/projects/{projectId}/members`

| Method | Endpoint | Body | Response | Description |
|--------|----------|------|----------|-------------|
| GET | `/api/projects/{projectId}/members` | — | `[{ userId, username, name, projectRole, invitedAt }]` | List project members |
| POST | `/api/projects/{projectId}/members` | `{ email: string (validated), role: ProjectRole }` | `{ userId, username, name, projectRole, invitedAt }` | Invite member (by email/username) |
| PATCH | `/api/projects/{projectId}/members/{memberId}` | `{ role: ProjectRole }` | `{ userId, username, name, projectRole, invitedAt }` | Update member role |
| DELETE | `/api/projects/{projectId}/members/{memberId}` | — | `204 No Content` | Remove member |

### 4. Chat — AI Code Generation

| Method | Endpoint | Body | Response | Description |
|--------|----------|------|----------|-------------|
| POST | `/api/chat/stream` | `{ message: string, projectId: Long }` | **SSE Stream** — `text/event-stream` | Send message to AI, receive streaming response. Returns `ServerSentEvent<String>` chunks. |
| GET | `/api/projects/{projectId}/chat/history` | — | `[{ id, chatSession, role, events, content, tokenUsed, createdAt }]` | Get chat history for a project |

**Streaming Chat Response Format (SSE):**
The AI responds with XML-tagged content that gets parsed into ChatEvents:
```xml
<message phase="start">I'll fix the streaming issue.</message>
<tool args="src/App.tsx">Reading **App.tsx**...</tool>
<message phase="planning">I see the issue. I need to wrap the app in the provider.</message>
<file path="src/App.tsx">...full file content...</file>
<message phase="completed">Done! Updated App.tsx with the provider.</message>
```

**ChatEvent types in the response:**
- `THOUGHT` — "Thought for Xs" (thinking duration)
- `MESSAGE` — Markdown text (phase: start, planning, completed)
- `FILE_EDIT` — File creation/modification with path and content
- `TOOL_LOG` — Tool usage log with args metadata

### 5. Files — `/api/projects/{projectId}/files`

| Method | Endpoint | Response | Description |
|--------|----------|----------|-------------|
| GET | `/api/projects/{projectId}/files` | `[{ path: string }]` | Get file tree (list of all file paths) |
| GET | `/api/projects/{projectId}/files/{*path}` | `{ path: string, content: string }` | Get file content (e.g., `/api/projects/42/files/src/App.tsx`) |

### 6. Billing & Subscriptions — Stripe Integration

| Method | Endpoint | Body | Response | Description |
|--------|----------|------|----------|-------------|
| GET | `/api/plans` | — | `[{ id, name, maxProjects, maxTokensPerDay, unlimitedAi, price }]` | List all active plans |
| GET | `/api/me/subscription` | — | `{ plan: PlanResponse, status: string, currentPeriodEnd: Instant, tokensUsedThisCycle: Long }` | Get current subscription |
| POST | `/api/payments/checkout` | `{ planId: Long }` | `{ checkoutUrl: string }` | Create Stripe Checkout session |
| POST | `/api/payments/portal` | — | `{ portalUrl: string }` | Open Stripe Customer Portal |
| POST | `/webhooks/payment` | Stripe webhook payload | `200 OK` | Stripe webhook handler (public, no auth) |

**Stripe Webhook Events Handled:**
- `checkout.session.completed` — Activates subscription
- `customer.subscription.updated` — Updates status, period, plan
- `customer.subscription.deleted` — Cancels subscription
- `invoice.paid` — Renews subscription period
- `invoice.payment_failed` — Marks as past due

### 7. Usage — `/api/usage`

| Method | Endpoint | Response | Description |
|--------|----------|----------|-------------|
| GET | `/api/usage/today` | `{ tokensUsed: int, tokensLimit: int, previewsRunning: int, previewsLimit: int }` | Get today's usage (currently returns null) |
| GET | `/api/usage/limits` | `{ planName: string, maxTokensPerDay: int, maxProjects: int, unlimitedAi: boolean }` | Get plan limits (currently returns null) |

---

## 🔑 AUTHORIZATION & PERMISSIONS

**Role-Based Access Control (Project-scoped):**

| Permission | OWNER | EDITOR | VIEWER |
|------------|-------|--------|--------|
| `VIEW` (view project, files, chat) | ✅ | ✅ | ✅ |
| `EDIT` (modify project, send chat) | ✅ | ✅ | ❌ |
| `DELETE` (delete project) | ✅ | ✅ | ❌ |
| `VIEW_MEMBER` (see member list) | ✅ | ✅ | ✅ |
| `MANAGE_MEMBERS` (invite/remove/update roles) | ✅ | ❌ | ❌ |

**Backend uses `@PreAuthorize` with Spring Security expressions like:**
- `@security.canEditProject(#projectId)`
- `@security.canViewMembers(#projectId)`

---

## 📦 ERROR HANDLING

All errors return a consistent JSON structure:
```json
{
  "status": 400,
  "message": "Bad request message",
  "timestamp": "2026-09-14T10:00:00Z",
  "errors": [
    { "field": "username", "message": "must not be blank" }
  ]
}
```

**HTTP Status Codes:**
| Error | Status | Example |
|-------|--------|---------|
| Validation error | 400 | Missing required fields |
| Bad request | 400 | User already exists, can't create project |
| Unauthorized | 401 | Invalid/missing JWT token |
| Forbidden | 403 | Access denied (insufficient project role) |
| Not found | 404 | Project/User/Subscription not found |

---

## 🏛️ INFRASTRUCTURE

### Services (Docker Compose)
- **PostgreSQL** (with pgvector): `localhost:5432`
- **MinIO** (S3-compatible object storage):
  - API: `localhost:9000`
  - Console: `localhost:9001`
  - Buckets: `projects` (file storage), `starter-projects` (templates), `project-ai-basket` (configured in app)
- **Frontend (your app):** Expected at `http://localhost:8080` (configurable via `client.url`)

### Project Template
When a new project is created, it auto-copies a **React + Vite + Tailwind + DaisyUI** starter template from the `starter-projects/react-vite-tailwind-daisyui-starter` MinIO bucket.

---

## 🎨 FRONTEND BUILD INSTRUCTIONS

### Tech Stack (React)
- **React 18** + **TypeScript**
- **Vite** as build tool
- **Tailwind CSS 4** + **daisyUI v5** for styling
- **React Router v6** for routing
- **TanStack Query (React Query)** for server state management
- **Axios** for HTTP client (with interceptors for JWT)
- **Lucide React** for icons
- **Zod** for form validation

### Pages to Build

#### 1. **Auth Pages**
- `/login` — Login form (email + password), link to signup
- `/signup` — Signup form (email + name + password), link to login

#### 2. **Dashboard / Projects List** (`/`)
- Grid/list of all projects the user owns or is a member of
- Each card shows: project name, last updated date
- "Create Project" button (opens modal with name input)
- User avatar/profile dropdown in header

#### 3. **Project Detail** (`/projects/:id`)
- **Split-pane layout:**
  - **Left sidebar:** File tree (collapsible, showing project files)
  - **Center:** Code editor / file viewer (when a file is selected)
  - **Right panel:** Chat interface with AI
- **Top bar:** Project name (editable), members dropdown, settings

#### 4. **Chat Interface** (within Project Detail)
- Chat message list with:
  - User messages (right-aligned, styled bubbles)
  - Assistant messages with:
    - `THOUGHT` events → Collapsible "Thinking..." section with duration
    - `MESSAGE` events → Rendered markdown
    - `FILE_EDIT` events → File card showing path + "View File" link
    - `TOOL_LOG` events → Tool usage indicator
- **Input bar** at bottom: text input + send button
- **Streaming support:** Use `EventSource` or `fetch` with `ReadableStream` to handle SSE from `/api/chat/stream`
- Auto-scroll to bottom on new messages

#### 5. **File Viewer** (within Project Detail)
- When clicking a file in the tree, fetch content from `/api/projects/{id}/files/{path}`
- Display with syntax highlighting (use a library like Prism.js or highlight.js)
- Show file path as breadcrumb

#### 6. **Project Members Page** (`/projects/:id/members`)
- List of members with: name, email, role badge, invited date
- Invite member form (email + role select)
- Role change dropdown (owner can change)
- Remove member button (owner can remove)

#### 7. **Settings / Subscription** (`/settings` or `/billing`)
- Current subscription status card
- List of available plans with pricing
- "Upgrade" button → redirects to Stripe Checkout URL
- "Manage Subscription" button → redirects to Stripe Customer Portal
- Usage stats (tokens used today, limit)

#### 8. **Pricing Page** (`/pricing`) — Optional
- Display all plans with features
- CTA buttons to checkout

### Key UI Patterns

#### Authentication Flow
```
1. Store JWT in localStorage on login/signup
2. Axios interceptor adds `Authorization: Bearer <token>` to all requests
3. On 401 response → redirect to /login, clear token
4. Protected routes check for token presence
```

#### Streaming Chat (SSE)
```typescript
// Use fetch with ReadableStream for SSE
const response = await fetch('/api/chat/stream', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({ message, projectId })
});

const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
  const { done, value } = await reader.read();
  if (done) break;
  const chunk = decoder.decode(value);
  // Parse SSE format: "data: <content>\n\n"
  // Append to chat UI progressively
}
```

#### File Tree Component
- Fetch from `GET /api/projects/{id}/files`
- Build a tree structure from flat `[{ path }]` array (e.g., `src/App.tsx` → `src/ → App.tsx`)
- Click to fetch and display file content

### Design Guidelines
- **Theme:** Modern, dark mode by default with daisyUI themes (e.g., "night", "dracula", or "business")
- **Colors:** Use daisyUI semantic classes (`btn-primary`, `bg-base-100`, `text-base-content`)
- **Typography:** Choose a distinctive font (not generic Inter/Roboto)
- **Spacing:** Tailwind spacing utilities (`p-*`, `gap-*`, `space-y-*`)
- **Animations:** Subtle transitions, loading skeletons for async data
- **Layout:** Sidebar + main content pattern for project workspace
- **Responsive:** Mobile-first, collapse sidebar on smaller screens

### Error States to Handle
- Empty states (no projects, no files, no chat history)
- Loading states (skeleton loaders)
- 401 → redirect to login
- 403 → "Access Denied" page
- 404 → "Not Found" page
- Network errors → toast notifications

---

## 📝 SAMPLE API RESPONSES

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "john@example.com",
    "name": "John Doe"
  }
}
```

### Projects List Response
```json
[
  {
    "id": 1,
    "projectName": "My React App",
    "createdAt": "2026-09-14T10:00:00Z",
    "updatedAt": "2026-09-14T12:30:00Z"
  }
]
```

### Project Detail Response
```json
{
  "id": 1,
  "name": "My React App",
  "createdAt": "2026-09-14T10:00:00Z",
  "updatedAt": "2026-09-14T12:30:00Z",
  "owner": {
    "id": 1,
    "username": "john@example.com",
    "name": "John Doe"
  }
}
```

### File Tree Response
```json
[
  { "path": "package.json" },
  { "path": "src/App.tsx" },
  { "path": "src/main.tsx" },
  { "path": "src/index.css" },
  { "path": "vite.config.ts" },
  { "path": "tailwind.config.js" }
]
```

### Chat History Response
```json
[
  {
    "id": 1,
    "role": "USER",
    "content": "Add a header component",
    "tokenUsed": 0,
    "createdAt": "2026-09-14T10:05:00Z",
    "events": []
  },
  {
    "id": 2,
    "role": "ASSISTANT",
    "content": "Assistant Message here...",
    "tokenUsed": 250,
    "createdAt": "2026-09-14T10:05:30Z",
    "events": [
      {
        "id": 1,
        "type": "THOUGHT",
        "sequenceOrder": 0,
        "content": "Thought for 3s",
        "filePath": null,
        "metadata": null
      },
      {
        "id": 2,
        "type": "MESSAGE",
        "sequenceOrder": 1,
        "content": "I'll create a header component for you.",
        "filePath": null,
        "metadata": null
      },
      {
        "id": 3,
        "type": "FILE_EDIT",
        "sequenceOrder": 2,
        "content": "import React from 'react';\n\nexport function Header() {\n  return <header>...</header>;\n}",
        "filePath": "src/components/Header.tsx",
        "metadata": null
      },
      {
        "id": 4,
        "type": "TOOL_LOG",
        "sequenceOrder": 3,
        "content": "Reading **src/App.tsx**...",
        "filePath": null,
        "metadata": "src/App.tsx"
      }
    ]
  }
]
```

### Subscription Response
```json
{
  "plan": {
    "id": 1,
    "name": "Pro",
    "maxProjects": 10,
    "maxTokensPerDay": 100000,
    "unlimitedAi": true,
    "price": "29.99"
  },
  "status": "ACTIVE",
  "currentPeriodEnd": "2026-10-14T10:00:00Z",
  "tokensUsedThisCycle": 45000
}
```

### Error Response
```json
{
  "status": 404,
  "message": "Project with id 99 not found",
  "timestamp": "2026-09-14T10:00:00Z",
  "errors": null
}
```

---

## 🚀 GETTING STARTED

1. Backend runs on `http://localhost:8080` (Spring Boot default)
2. Set `client.url` in `application.yaml` to your frontend URL
3. Frontend should proxy API requests to the backend during development
4. Store JWT in `localStorage` and attach to all requests
5. Handle SSE streaming for the chat endpoint

**Base URL:** `http://localhost:8080`

**Development Proxy (vite.config.ts):**
```typescript
export default defineConfig({
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/webhooks': 'http://localhost:8080'
    }
  }
})
```
