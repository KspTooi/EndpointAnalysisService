import type CommonIdDto from "@/commons/entity/CommonIdDto";
import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";

export interface GetCollectionTreeVo {
  id: string; //主键ID
  parentId: string | null; //父级ID null:顶级
  name: string; //集合名称
  kind: number; //集合类型 0:请求 1:组
  reqMethod: number; //请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS
  children: GetCollectionTreeVo[]; //子节点
}

export interface AddCollectionDto {
  parentId: string | null; //父级ID null:顶级
  name: string; //集合名称
  kind: number; //集合类型 0:请求 1:组
  reqUrl: string; //请求URL
  reqUrlParamsJson: string; //请求URL参数JSON
  reqMethod: number; //请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS
  reqHeaderJson: string; //请求头JSON
  reqBodyKind: number; //请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded
  reqBodyJson: string; //请求体JSON
}

export interface MoveCollectionDto {
  nodeId: string; //对象ID
  targetId: string | null; //目标ID null:顶级
  kind: number; //移动方式 0:顶部 1:底部 2:内部
}

export interface EditCollectionDto {
  id: string; //主键ID
  name: string; //集合名称
  reqUrl: string; //请求URL
  reqUrlParamsJson: string; //请求URL参数JSON
  reqMethod: number; //请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS
  reqHeaderJson: string; //请求头JSON
  reqBodyKind: number; //请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded
  reqBodyJson: string; //请求体JSON
}

export interface GetCollectionDetailsVo {
  id: string; //主键ID
  parentId: string | null; //父级ID null:顶级
  name: string; //集合名称
  kind: number; //集合类型 0:请求 1:组
  reqUrl: string; //请求URL
  reqUrlParamsJson: string; //请求URL参数JSON
  reqMethod: number; //请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS
  reqHeaderJson: string; //请求头JSON
  reqBodyKind: number; //请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded
  reqBodyJson: string; //请求体JSON
  seq: number; //排序
}

export default {
  /**
   * 获取请求集合树
   * @returns 请求集合树
   */
  getCollectionTree: async (): Promise<Result<GetCollectionTreeVo[]>> => {
    return await Http.postEntity<Result<GetCollectionTreeVo[]>>("/collection/getCollectionTree", {});
  },

  /**
   * 新增请求集合
   * @param dto 新增请求集合
   * @returns 新增请求集合
   */
  addCollection: async (dto: AddCollectionDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/collection/addCollection", dto);
  },

  /**
   * 移动请求集合
   * @param dto 移动请求集合
   * @returns 移动请求集合
   */
  moveCollection: async (dto: MoveCollectionDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/collection/moveCollection", dto);
  },

  /**
   * 复制请求集合
   * @param dto 复制请求集合
   * @returns 复制请求集合
   */
  copyCollection: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/collection/copyCollection", dto);
  },

  /**
   * 编辑请求集合
   * @param dto 编辑请求集合
   * @returns 编辑请求集合
   */
  editCollection: async (dto: EditCollectionDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/collection/editCollection", dto);
  },

  /**
   * 获取请求集合详情
   * @param dto 获取请求集合详情
   * @returns 获取请求集合详情
   */
  getCollectionDetails: async (dto: CommonIdDto): Promise<Result<GetCollectionDetailsVo>> => {
    return await Http.postEntity<Result<GetCollectionDetailsVo>>("/collection/getCollectionDetails", dto);
  },

  /**
   * 删除请求集合
   * @param dto 删除请求集合
   * @returns 删除请求集合
   */
  removeCollection: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/collection/removeCollection", dto);
  },
};
