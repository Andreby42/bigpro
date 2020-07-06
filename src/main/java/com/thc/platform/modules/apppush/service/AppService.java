package com.thc.platform.modules.apppush.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.modules.apppush.dao.AppDao;
import com.thc.platform.modules.apppush.entity.AppEntity;

@Service
public class AppService extends ServiceImpl<AppDao, AppEntity> {

}
