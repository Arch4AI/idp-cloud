package com.idp.cloud.plugin;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * Lombok插件适配
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/7 9:48 AM
 **/
public class LombokPlugin extends PluginAdapter {

    public LombokPlugin() {
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine("* 表说明:");
        topLevelClass.addJavaDocLine("*  "+ introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine("* ");
        topLevelClass.addJavaDocLine("* 表名:" + introspectedTable.getTableConfiguration().getTableName());
        topLevelClass.addJavaDocLine("* Date: " + this.date2Str(new Date()));
        topLevelClass.addJavaDocLine("*/");
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine("* 大字段实体");
        topLevelClass.addJavaDocLine("* 表说明:");
        topLevelClass.addJavaDocLine("*  "+ introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine("* ");
        topLevelClass.addJavaDocLine("* 表名:" + introspectedTable.getTableConfiguration().getTableName());
        topLevelClass.addJavaDocLine("* Date: " + this.date2Str(new Date()));
        topLevelClass.addJavaDocLine("*/");
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine("* 表主键对象");
        topLevelClass.addJavaDocLine("* 表说明:");
        topLevelClass.addJavaDocLine("*  "+ introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine("* ");
        topLevelClass.addJavaDocLine("* 表名:" + introspectedTable.getTableConfiguration().getTableName());
        topLevelClass.addJavaDocLine("* Date: " + this.date2Str(new Date()));
        topLevelClass.addJavaDocLine("*/");
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        boolean suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
        // 如果没有开通且支持自定义内容的时候就set进去
        if(commentGenerator == null || suppressAllComments){
            field.addJavaDocLine("/**");
            field.addJavaDocLine("* 字段说明:");
            field.addJavaDocLine("* " + introspectedColumn.getRemarks());
            field.addJavaDocLine("* ");
            field.addJavaDocLine("* 对应表中的列: "+introspectedTable.getTableConfiguration().getTableName()+"."+introspectedColumn.getActualColumnName());
            field.addJavaDocLine("* ");
            field.addJavaDocLine("*/ ");
        }
        return true;
    }



    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    private String date2Str(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }
}
