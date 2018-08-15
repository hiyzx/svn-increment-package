# svn-increment-package

该项目是基于liubq100000的https://github.com/liubq100000/BHChangePacker 进行修改,感谢.

## 如何使用
1. 下载全新的svn项目,并进行clean和install
2. 修改PackerMain下的参数
    1. TARGET_PROJECT修改为打包项目的target目录
    2. SVN_URL修改为顶级pom对应的url
    3. USERNAME,PASSWORD,PROJECT_VERSION
    4. lastVersion修改开始打包的version
3. PackerMain的方法addNewJar()
    1. 第一次需将聚合模块依赖的jar包全部添加进来
    2. 每次打包都将新增的普通jar包添加进来
4. PackerMain的方法commonPackage()
    1. 将需要打包的war包添加进来
5. 运行main方法(需安装lombok),生成内容在C:\Users\Administrator\Desktop\war包\war-MM-dd-HH