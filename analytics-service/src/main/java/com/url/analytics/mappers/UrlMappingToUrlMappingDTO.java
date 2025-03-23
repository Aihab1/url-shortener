package com.url.analytics.mappers;

import com.url.analytics.dto.UrlMappingDTO;
import com.url.analytics.models.UrlMapping;
import com.url.analytics.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {User.class})
public interface UrlMappingToUrlMappingDTO {
  UrlMappingToUrlMappingDTO INSTANCE = Mappers.getMapper(UrlMappingToUrlMappingDTO.class);

  @Mapping(target = "username", source = "user.username")
  UrlMappingDTO map(UrlMapping urlMapping);
}
