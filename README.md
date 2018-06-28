# svn-increment-package

该项目是基于liubq100000的https://github.com/liubq100000/BHChangePacker 进行修改,感谢.

## 如何使用
1. 下载全新的svn项目,并进行clean和install
2. 修改PackerMain下的参数
    1. TARGET_PROJECT修改为打包项目的target目录
    2. SVN_URL修改为顶级pom对应的url
    3. USERNAME,PASSWORD,PROJECT_VERSION
    4. lastVersion修改开始打包的version
3. 调用addNewJar打包项目内的jar包以及从lastVersion开始内pom中新增的jar
4. 调用commonPackage打包war包
5. 生成内容在C:\Users\Administrator\Desktop\war包\war-MM-dd-HH