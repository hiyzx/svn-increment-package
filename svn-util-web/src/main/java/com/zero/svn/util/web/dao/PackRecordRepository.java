package com.zero.svn.util.web.dao;

import com.zero.svn.util.web.model.po.PackRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Repository
public interface PackRecordRepository extends JpaRepository<PackRecord, Integer> {

}
