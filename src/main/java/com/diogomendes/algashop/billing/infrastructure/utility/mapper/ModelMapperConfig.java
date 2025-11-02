package com.diogomendes.algashop.billing.infrastructure.utility.mapper;

import com.diogomendes.algashop.billing.application.utility.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.modelmapper.convention.MatchingStrategies.STRICT;
import static org.modelmapper.convention.NamingConventions.NONE;

@Configuration
public class ModelMapperConfig {

    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        configuration(modelMapper);
        return modelMapper::map;
    }

    private void configuration(ModelMapper modelMapper) {
        modelMapper.getConfiguration()
                .setSourceNamingConvention(NONE)
                .setDestinationNamingConvention(NONE)
                .setMatchingStrategy(STRICT);
    }

}
