/**
 * 分页视图数据接口
 * T - 列表行的类型
 */
export default interface PageableView<T> {


    //0:正常，1:业务异常，2:内部服务器错误，3:请求异常
    code: number;

    //错误信息
    message: string;
    
    /**
     * 当前页的数据列表
     */
    data: T[];

    /**
     * 总记录数
     */
    total: number;

}