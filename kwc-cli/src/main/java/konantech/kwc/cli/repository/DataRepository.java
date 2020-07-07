package konantech.kwc.cli.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import konantech.kwc.cli.entity.DataEntity;
import konantech.kwc.cli.entity.KHash;


@Repository
public class DataRepository {
	@PersistenceContext
	EntityManager em;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
//	private final LocalDateTime minTableMon = CommonUtil.stringToLocalDay("20200301", "yyyyMMdd");
	
	@Autowired
	CrawlRepository crawlRepository;
	
	
	@Transactional
	public void saveData(String table, DataEntity data, LocalDateTime minTableMon) {
		if(data.getWrite_time().isBefore(minTableMon))
			return ;
		
		Query query = em.createNativeQuery("insert into " + table + "( channel, site_name, board_name, uniqkey, url,"
				+ " title, doc, write_id , write_time , crawled_time , update_time , pseudo) values  "
				+ " (?1, ?2, ?3, ?4, ?5 , ?6, ?7, ?8, ?9, ?10 ,?11 ,?12)");
		
		query.setParameter(1, data.getChannel());
		query.setParameter(2, data.getSite_name());
		query.setParameter(3, data.getBoard_name());
		query.setParameter(4, data.getUniqkey());
		query.setParameter(5, data.getUrl());
		query.setParameter(6, data.getTitle());
		query.setParameter(7, data.getDoc());
		query.setParameter(8, data.getWrite_id());
		query.setParameter(9, data.getWrite_time());
		query.setParameter(10, data.getCrawled_time());
		query.setParameter(11, data.getUpdate_time());
		query.setParameter(12, data.getPseudo());
		
		
		query.executeUpdate();
		
	}
	
	
	@Transactional
	public void saveDataList(List<DataEntity> dataList, LocalDateTime minTableMon) {
		StringBuffer sb = new StringBuffer();
//		em.unwrap(Session.class).setJdbcBatchSize(10);
		
		for(DataEntity data : dataList) {
			if(data.getWrite_time().isBefore(minTableMon))
				continue;
			Query query = em.createNativeQuery("insert into " + data.getTableName() + "( channel, site_name, board_name, uniqkey, url,"
					+ " title, doc, write_id , write_time , crawled_time , update_time , pseudo) values  "
					+ " (?1, ?2, ?3, ?4, ?5 , ?6, ?7, ?8, ?9, ?10 ,?11 ,?12)");
			
			query.setParameter(1, data.getChannel());
			query.setParameter(2, data.getSite_name());
			query.setParameter(3, data.getBoard_name());
			query.setParameter(4, data.getUniqkey());
			query.setParameter(5, data.getUrl());
			query.setParameter(6, data.getTitle());
			query.setParameter(7, data.getDoc());
			query.setParameter(8, data.getWrite_id());
			query.setParameter(9, data.getWrite_time());
			query.setParameter(10, data.getCrawled_time());
			query.setParameter(11, data.getUpdate_time());
			query.setParameter(12, data.getPseudo());
			
			query.executeUpdate();
		}
		
	}
	
	@Transactional
	public void updateDataList(List<DataEntity> dataList, LocalDateTime minTableMon) {
		
//		em.unwrap(Session.class).setJdbcBatchSize(10);
		String updateQuery = "update %s set ";
		
		for(DataEntity data : dataList) {
			if(data.getWrite_time().isBefore(minTableMon))
				continue;
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(updateQuery, data.getTableName()));
			sb.append("channel=?1 , site_name=?2, board_name=?3, url=?4, title=?5, doc=?6, write_id=?7, write_time=?8, crawled_time=?9, update_time=?10, pseudo=?11 ");
			sb.append("where uniqkey = ?12");
			
			Query query = em.createNativeQuery(sb.toString());
			query.setParameter(1, data.getChannel());
			query.setParameter(2, data.getSite_name());
			query.setParameter(3, data.getBoard_name());
			query.setParameter(4, data.getUrl());
			query.setParameter(5, data.getTitle());
			query.setParameter(6, data.getDoc());
			query.setParameter(7, data.getWrite_id());
			query.setParameter(8, data.getWrite_time());
			query.setParameter(9, data.getCrawled_time());
			query.setParameter(10, data.getUpdate_time());
			query.setParameter(11, data.getPseudo());
			query.setParameter(12, data.getUniqkey());
			query.executeUpdate();
		}
		
	}
	
	public KHash getHashed(String hashed) {
		TypedQuery<KHash> typedQuery = em.createQuery("select h from KHash h where h.hashed = :hashed", KHash.class);
		typedQuery.setParameter("hashed", hashed);
		List<KHash> hashList = typedQuery.getResultList();
		if(hashList.size() > 1) {
			logger.error("notUniqueException URL : " + hashed);
		}
			
		if(hashList.size() > 0) {//중복일때
			KHash hash = hashList.get(0);
			return hash;
		}
		else {
			return null;
		}
	}
	
}
