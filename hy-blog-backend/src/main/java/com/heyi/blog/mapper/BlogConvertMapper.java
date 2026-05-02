package com.heyi.blog.mapper;

import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.dto.BlogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BlogConvertMapper {

    BlogConvertMapper INSTANCE = Mappers.getMapper(BlogConvertMapper.class);

    Blog toBlog(BlogDTO blogDTO);
}