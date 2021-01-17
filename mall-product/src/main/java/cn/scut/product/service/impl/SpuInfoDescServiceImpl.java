package cn.scut.product.service.impl;

import cn.scut.product.dao.SpuInfoDao;
import cn.scut.product.dao.SpuInfoDescDao;
import cn.scut.product.entity.SpuInfoDescEntity;
import cn.scut.product.service.SpuInfoDescService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity>implements SpuInfoDescService {

}
