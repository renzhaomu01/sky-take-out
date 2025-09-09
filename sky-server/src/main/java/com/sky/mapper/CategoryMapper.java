package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    /**
     * 添加新分类
     * @param category
     */
    @Insert("insert into category (id,type,name,sort,status,create_time,update_time,create_user,update_user)" + "values" + "(#{id},#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);
}
