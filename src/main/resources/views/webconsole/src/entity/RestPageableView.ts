/**
 * 分页视图数据接口
 * T - 列表行的类型
 */
export default interface PageableView<T> {

    /**
     * 当前页的数据列表
     */
    data: T[];

    /**
     * 总记录数
     */
    total: number;

}