import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetDataSourceListDto extends PageQuery {
  name?: string; // 数据源名称
  code?: string; // 数据源编码
  kind?: number; // 数据源类型 0:MYSQL
  drive?: string; // JDBC驱动
  url?: string; // 连接字符串
  username?: string; // 连接用户名
  password?: string; // 连接密码
  dbSchema?: string; // 默认模式
}

/**
 * 列表VO
 */
export interface GetDataSourceListVo {
  id: string; // 主键ID
  name: string; // 数据源名称
  code: string; // 数据源编码
  kind: number; // 数据源类型 0:MYSQL
  drive: string; // JDBC驱动
  url: string; // 连接字符串
  username: string; // 连接用户名
  password: string; // 连接密码
  dbSchema: string; // 默认模式
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
}

/**
 * 详情VO
 */
export interface GetDataSourceDetailsVo {
  id: string; // 主键ID
  name: string; // 数据源名称
  code: string; // 数据源编码
  kind: number; // 数据源类型 0:MYSQL
  drive: string; // JDBC驱动
  url: string; // 连接字符串
  username: string; // 连接用户名
  password: string; // 连接密码
  dbSchema: string; // 默认模式
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
}

/**
 * 新增DTO
 */
export interface AddDataSourceDto {
  name: string; // 数据源名称
  code: string; // 数据源编码
  kind: number; // 数据源类型 0:MYSQL
  drive: string; // JDBC驱动
  url: string; // 连接字符串
  username: string; // 连接用户名
  password: string; // 连接密码
  dbSchema: string; // 默认模式
}

/**
 * 编辑DTO
 */
export interface EditDataSourceDto {
  id: string; // 主键ID
  name: string; // 数据源名称
  code: string; // 数据源编码
  kind: number; // 数据源类型 0:MYSQL
  drive: string; // JDBC驱动
  url: string; // 连接字符串
  username: string; // 连接用户名
  password: string; // 连接密码
  dbSchema: string; // 默认模式
}

export default {
  /**
   * 获取数据源表列表
   */
  getDataSourceList: async (dto: GetDataSourceListDto): Promise<PageResult<GetDataSourceListVo>> => {
    return await Http.postEntity<PageResult<GetDataSourceListVo>>("/dataSource/getDataSourceList", dto);
  },

  /**
   * 获取数据源表详情
   */
  getDataSourceDetails: async (dto: CommonIdDto): Promise<GetDataSourceDetailsVo> => {
    const result = await Http.postEntity<Result<GetDataSourceDetailsVo>>("/dataSource/getDataSourceDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增数据源表
   */
  addDataSource: async (dto: AddDataSourceDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/dataSource/addDataSource", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑数据源表
   */
  editDataSource: async (dto: EditDataSourceDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/dataSource/editDataSource", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除数据源表
   */
  removeDataSource: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/dataSource/removeDataSource", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
