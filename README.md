# svn-increment-package
### 背景
前公司每次更新项目只能使用增量包的形式,增量包需要注意文件层级结构以及有改动的文件,比较麻烦,所以有了该项目.  
### 感谢
该项目是基于@liubq100000的https://github.com/liubq100000/BHChangePacker 进行修改,感谢.
### 说明
将最新的代码编译,然后根据svn版本号,获取期间变动的文件名称,到target下获取对应的class文件.
## 如何使用
1. 如果是首次打包发布的话,直接发全量包
2. 该项目针对增量包
    1. 修改PackerMain中ConfigDto的参数,并执行main方法(需安装lombok)
    2. 到savePath路径下找到对应文件(注意配置文件的修改,及删除文件的说明)