package structure

```txt
com.artemis.boot
├── annotation          # 自定义注解
├── aop                 # AOP切面
├── common              # 公共通用模块
│   ├── constant        # 系统常量
│   ├── enums           # 全局枚举
│   ├── exception       # 全局异常
│   ├── result          # 统一返回封装
│   └── util            # 工具类
├── config              # Spring配置类
├── security            # JWT、认证授权相关
├── base                # 基础父类
└── biz                 # 业务代码包（RBAC相关业务全部放这里）
    ├── controller      # 控制器层
    ├── service         # 业务接口
    ├── impl            # service实现类
    ├── mapper          # MyBatis‑Plus Mapper
    ├── entity          # 数据库实体
    ├── dto             # 请求入参
    └── vo              # 响应出参
```
---
**resources 资源目录**

```txt
src/main/resources
├── application.yml
├── application-dev.yml
├── application-test.yml
├── application-prod.yml
└── db/migration        # Flyway数据库脚本

```