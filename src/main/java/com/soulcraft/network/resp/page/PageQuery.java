package com.soulcraft.network.resp.page;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * <pre>
 *     分页查询基础对象
 * </pre>
 *
 * @author scott
 * @since 2022年03月10日
 */
@Data
public class PageQuery implements Serializable {

    @Min(value = 1, message = "[页码]参数不能小于1")
    protected int pageNum = 1;

    @Min(value = 1, message = "[页数据条数]参数不能效于1")
    protected int pageSize = 5;

}
