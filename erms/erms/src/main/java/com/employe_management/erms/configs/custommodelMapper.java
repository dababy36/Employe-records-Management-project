package com.employe_management.erms.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class custommodelMapper {
    //model mapper used to map the entity to dto and vice versa
    //Create a bean for ModelMapper

        @Bean
        public ModelMapper modelMapper() {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setSkipNullEnabled(true) // Ignore null values during mapping
                    .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
            return modelMapper;
        }
    }



