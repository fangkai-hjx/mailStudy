package cn.scut.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-18 10:45:51
 */
@Data
@TableName("man_blog")
public class BlogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * blog的Id
	 */
	@TableId
	private Long id;
	/**
	 * 文章标题
	 */
	private String title;
	/**
	 * 文章链接
	 */
	private String link;
	/**
	 * 个人描述
	 */
	private String descript;
	/**
	 * 个人喜欢
	 */
	private Integer isLike;
	/**
	 * 是否标注喜欢
	 */
	private Integer onandoff;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 1表示删除
	 */
	private Integer isDelete;
	/**
	 * 跟新时间
	 */
	private Date updateTime;

}
