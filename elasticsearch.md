# Elasticsearch 설정

### 인증 계정/패스워드 설정

1. \(es-home\)/bin/elasticsearch-keystore create  명령 &gt; elasticsearch.xml 생성
2. \(es-home\)/config/elasticsearch.xml   설정 추가

```text
xpack.security.enabled: true
```

3. setup-password 

![](.gitbook/assets/image%20%282%29.png)



