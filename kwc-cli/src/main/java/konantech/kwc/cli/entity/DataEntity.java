package konantech.kwc.cli.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import konantech.kwc.cli.common.CommonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataEntity {
	
	
	String channel;
	String site_name;
	String board_name;
	String uniqkey;
	String url;
	String title;
	String doc;
	String write_id;
	LocalDateTime write_time;
	LocalDateTime crawled_time;
	LocalDateTime update_time;
	String pseudo;
	
	String tableName;
	
	public DataEntity() {}

	public DataEntity(String channel, String site_name, String board_name, String uniqkey, String url, String title,
			String doc, String write_id, LocalDateTime write_time, LocalDateTime crawled_time,
			LocalDateTime update_time, String pseudo) {
		super();
		this.channel = channel;
		this.site_name = site_name;
		this.board_name = board_name;
		this.uniqkey = uniqkey;
		this.url = url;
		this.title = title;
		this.doc = doc;
		this.write_id = write_id;
		this.write_time = (write_time == null)? crawled_time:write_time;
		this.crawled_time = crawled_time;
		this.update_time = update_time;
		this.pseudo = pseudo;
		setMonthTableName();
	}
	
	
	public void setMonthTableName() {
		this.tableName = "data"+CommonUtils.DateTimeToStr(this.write_time, "yyyyMM");
	}
	
}
