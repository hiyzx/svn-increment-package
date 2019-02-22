package com.zero.svn.util.web.dao;

import com.zero.svn.util.web.model.po.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {

    Config findByProjectName(String projectName);
}
