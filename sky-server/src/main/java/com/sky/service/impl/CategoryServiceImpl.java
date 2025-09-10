package com.sky.service.impl;

import com.alibaba.fastjson.serializer.BeanContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        //将前端传过来的DTO数据copy到新建的java对象中方便进行操作
        BeanUtils.copyProperties(categoryDTO,category);
        //默认分类禁用
        category.setStatus(StatusConstant.DISABLE);
        //创建时间和修改时间使用当前系统时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        //修改人id在线程中获取
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     */
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        //使用了一个插件进行sql语句的拼接，目前PageResult里有两条变量，就是一个总记录数和一个数据集合，
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        //将page在前端传入的DTO对象获取到page和pageSize
        return new PageResult(page.getTotal(),page.getResult());
    }


    /**
     * 修改菜品状态，启用或者禁用
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //根据方法参数进行修改状态，使用builder方法创建对象并且修改状态值，并同时更新修改时间和修改人
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }
}
