package com.url.shortener.mappers;

import com.url.shortener.dto.UrlMappingDTO;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {User.class})
public interface UrlMappingToUrlMappingDTO {
  UrlMappingToUrlMappingDTO INSTANCE = Mappers.getMapper(UrlMappingToUrlMappingDTO.class);

  @Mapping(target = "username", source = "user.username")
  UrlMappingDTO map(UrlMapping urlMapping);
}
