package com.ljt.study.elk.es.service;

import com.ljt.study.elk.es.doc.TestDoc;
import com.ljt.study.elk.es.mapper.TestDocMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LiJingTang
 * @date 2020-11-17 09:57
 */
@Service
public class TestDocService {

    @Autowired
    private TestDocMapper testDocMapper;

    public void save(TestDoc doc) {
        testDocMapper.save(doc);
    }

}
