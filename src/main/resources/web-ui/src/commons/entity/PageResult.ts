/**
 * 通用分页结果接口
 * T - 列表项类型
 */
export default interface PageResult<T> {

    /**
     * 业务状态码
     * 0-正常，1-业务异常，2-内部服务器错误，3-请求异常
     */
    code: number;

    /**
     * 描述信息
     */
    message: string;

    /**
     * 返回数据集合
     */
    data: T[];

    /**
     * 总记录数
     */
    total: number;
}


