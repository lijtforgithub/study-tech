## 基础概念
- 倒排索引：根据关键字建索引 **空间换时间**
    1. 包含这个关键词的 doc list
    2. 关键词在每个doc中出现的次数 TF term frequency
    3. 关键词在整个索引中出现的次数 IDF inverse doc frequency **IDF越高 相关度越低**
    4. 关键词在当前doc中出现的次数
    5. 每个doc的长度，越长相关度越低
    6. 包含这个关键词的所有doc的平均长度
