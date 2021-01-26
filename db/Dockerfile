FROM mysql:5.7

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./mall_admin.sql /docker-entrypoint-initdb.d
COPY ./mall_oms.sql /docker-entrypoint-initdb.d
COPY ./mall_pms.sql /docker-entrypoint-initdb.d
COPY ./mall_sms.sql /docker-entrypoint-initdb.d
COPY ./mall_ums.sql /docker-entrypoint-initdb.d
COPY ./mall_wms.sql /docker-entrypoint-initdb.d