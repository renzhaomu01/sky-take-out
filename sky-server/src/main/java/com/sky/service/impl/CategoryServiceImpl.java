package com.sky.service.impl;

import com.alibaba.fastjson.serializer.BeanContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
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
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


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

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //此处在书写时有一个疑惑，就是在我将前端传来的属性放在DTO对象里了，使用了Beanutils工具类进行复制了属性到我的java实体类category中，其中由于前端传来的参数只有id，name，sort，type这四个参数，所以在修改category参数时只修改除了上面四个参数后，再同步更新修改时间和修改人就好了

        //同步更新时间
        category.setUpdateTime(LocalDateTime.now());
        //同步更新人
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    public void deleteById(Long id) {
        //统计前端传来的id是否有关联菜品或者套餐
        Integer conunt = dishMapper.countByCategoryId(id);
        if(conunt > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        conunt = setmealMapper.countByCategoryId(id);
        if(conunt > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.deleteById(id);
    }
}
