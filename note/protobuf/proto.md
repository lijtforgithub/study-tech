#### 消息定义
- 字段规则
  * optional -> 字段可出现 0 次或1次
  * required -> 字段只能也必须出现 1 次
  * repeated -> 字段可出现任意多次（包括 0）
- 类型：int32、int64、sint32、sint64、string、32-bit ....
- 字段编号：0 ~ 536870911（除去 19000 到 19999 之间的数字）
```protobuf
message SearchRequest {
  // 字段规则 类型 名称 = 字段编号;
  required string query = 1;  // 查询字符串
  optional int32 page_number = 2;  // 第几页
  optional int32 result_per_page = 3;  // 每页的结果数
}

message SearchResponse {
  reserved 2, 15, 9 to 11;
  reserved "foo", "bar";
}
```

#### 生成java类
- $SRC_DIR: .proto 所在的源目录
- --cpp_out: 生成 c++ 代码
- $DST_DIR: 生成代码的目标目录
- xxx.proto: 要针对哪个 proto 文件生成接口代码

```protoc -I=$SRC_DIR --cpp_out=$DST_DIR $SRC_DIR/xxx.proto```