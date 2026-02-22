/**
 * 通用后端响应结果接口
 * T - 返回数据的类型
 */
export default interface Result<T> {
  /**
   * 业务状态码
   * 0 - 业务正常
   * 1 - 业务异常
   * 2 - 内部服务器错误
   */
  code: number;

  /**
   * 描述信息
   */
  message: string;

  /**
   * 返回数据
   */
  data: T;
}

export namespace Result {
  /**
   * 判断结果是否成功
   * @param result 结果
   * @returns 是否成功
   */
  export function isSuccess(result: Result<any>): boolean {
    if (result === null || result === undefined) {
      return false;
    }
    return result.code === 0;
  }

  /**
   * 判断结果是否失败
   * @param result 结果
   * @returns 是否失败
   */
  export function isError(result: Result<any>): boolean {
    if (result === null || result === undefined) {
      return true;
    }
    return result.code !== 0;
  }
}
