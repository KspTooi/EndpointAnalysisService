export default interface CommonIdDto {
  //通常为单个ID
  id?: string | null;

  //通常为批量ID 传入后与ID合并后进行批量操作(但通常不会与ID一起传入)
  ids?: string[] | null;
}
