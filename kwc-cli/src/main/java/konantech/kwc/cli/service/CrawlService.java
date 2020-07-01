package konantech.kwc.cli.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import konantech.kwc.cli.common.CommonUtils;
import konantech.kwc.cli.entity.Crawl;
import konantech.kwc.cli.entity.DataEntity;
import konantech.kwc.cli.entity.KHash;
import konantech.kwc.cli.repository.CrawlRepository;
import konantech.kwc.cli.repository.DataRepository;

@Service
public class CrawlService {

	@Autowired
	DataRepository dataRepository;
	
	@Autowired
	CrawlRepository crawlRepository;
	
	public void saveCrawl(List<Crawl> dataList, String ctrtStart) {
		List<DataEntity> insert = new ArrayList<DataEntity>();
		List<DataEntity> update = new ArrayList<DataEntity>();
		dataList.forEach((data) -> {
//			Optional<Crawl> op = crawlRepository.findByHashed(data.getHashed());
//			if(op.isPresent()) {
			KHash tab = dataRepository.getHashed(data.getHashed());
			if(tab != null) {
				data.setIdx(tab.getIdx());
				crawlRepository.save(data);
				update.add(new DataEntity(data.getChannel(), data.getSiteName(), data.getBoardName(), String.valueOf(tab.getIdx()), data.getUrl(), data.getTitle(), 
						data.getDoc(), data.getWriteId(), data.getWriteTime(), data.getCrawledTime(), data.getCrawledTime(), data.getPseudo()));
			}else {
				Crawl tmp = crawlRepository.save(data);
				insert.add(new DataEntity(data.getChannel(), data.getSiteName(), data.getBoardName(), String.valueOf(tmp.getIdx()), data.getUrl(), data.getTitle(), 
						data.getDoc(), data.getWriteId(), data.getWriteTime(), data.getCrawledTime(), data.getCrawledTime(), data.getPseudo()));
			}
		});
		
		
		LocalDateTime minDate = CommonUtils.stringToLocalDay(StringUtils.defaultIfEmpty(ctrtStart,"2017-01-01"), "yyyy-MM-dd");
		
		dataRepository.saveDataList(insert,minDate);
		dataRepository.updateDataList(update,minDate);
	}
}
