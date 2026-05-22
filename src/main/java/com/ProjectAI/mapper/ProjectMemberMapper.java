package com.ProjectAI.mapper;

import com.ProjectAI.dto.member.MemberResponse;
import com.ProjectAI.entity.ProjectMember;
import com.ProjectAI.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId" , source = "id")
    @Mapping(target = "projectRole" , constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(target = "userId" , source = "user.id")
    @Mapping(target = "email" , source = "user.email")
    @Mapping(target = "name" , source = "user.name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);

}
