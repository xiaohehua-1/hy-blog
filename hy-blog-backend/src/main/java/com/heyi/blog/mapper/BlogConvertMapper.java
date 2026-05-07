package com.heyi.blog.mapper;

import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.dto.BlogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 博客对象转换器（MapStruct）
 * BlogDTO → Blog 实体映射，编译期自动生成高性能转换代码，避免手动逐字段拷贝。
 */
@Mapper(componentModel = "spring")
public interface BlogConvertMapper {

    BlogConvertMapper INSTANCE = Mappers.getMapper(BlogConvertMapper.class);

    /**
     * BlogDTO 转 Blog 实体，将含 tagIds 的 DTO 映射为数据库实体
     */
    Blog toBlog(BlogDTO blogDTO);
}