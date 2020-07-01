package konantech.kwc.cli.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
@Table(name = "tb_crawl")
public class Crawl {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int idx;
	@Column
	String channel;
	@Column
	String siteName;
	@Column
	String boardName;
	@Column
	String uniqkey;
	@Column
	String url;
	@Column
	String title;
	@Column
	String doc;
	@Column
	String writeId;
	@Column
	LocalDateTime writeTime;
	@Column
	LocalDateTime crawledTime;
	@Column
	LocalDateTime updateTime;
	@Column
	String pseudo;
	@Column
	String wtimeStr;
	@Column
	String collector;
	@Column
	String hashed;
	
	public Crawl() {}
	@Builder
	public Crawl(String channel, String siteName, String boardName, String uniqkey, String url, String title,
			String doc, String writeId, LocalDateTime writeTime, LocalDateTime crawledTime, LocalDateTime updateTime,
			String pseudo) {
		super();
		this.channel = channel;
		this.siteName = siteName;
		this.boardName = boardName;
		this.uniqkey = uniqkey;
		this.url = url;
		this.title = title;
		this.doc = doc;
		this.writeId = writeId;
		this.writeTime = writeTime;
		this.crawledTime = crawledTime;
		this.updateTime = updateTime;
		this.pseudo = pseudo;
	}
	
	
}
