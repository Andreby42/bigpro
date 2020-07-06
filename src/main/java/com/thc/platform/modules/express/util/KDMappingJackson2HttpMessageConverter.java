package com.thc.platform.modules.express.util;


import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class KDMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public KDMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);  //加入text/html类型的支持
        mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);  //加入text/html类型的支持
        setSupportedMediaTypes(mediaTypes);// tag6
    }
}