package com.thc.platform.modules.job.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.modules.job.dao.JobLogDao;
import com.thc.platform.modules.job.entity.JobLogEntity;

@Service
public class JobLogService extends ServiceImpl<JobLogDao, JobLogEntity> {

}
