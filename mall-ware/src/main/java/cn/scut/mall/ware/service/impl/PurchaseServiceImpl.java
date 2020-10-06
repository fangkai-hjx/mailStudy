package cn.scut.mall.ware.service.impl;

import cn.scut.common.constant.WareConstant;
import cn.scut.mall.ware.entity.PurchaseDetailEntity;
import cn.scut.mall.ware.service.PurchaseDetailService;
import cn.scut.mall.ware.service.WareSkuService;
import cn.scut.mall.ware.vo.MergeVo;
import cn.scut.mall.ware.vo.PurchaseDoneVo;
import cn.scut.mall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.ware.dao.PurchaseDao;
import cn.scut.mall.ware.entity.PurchaseEntity;
import cn.scut.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnReceivePurchase(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)//新建/已分配
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());//状态为新建
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();//为空 的 情况下 采购单的id
        }
        //确入 采购单 的 转状态 为 0 或者 1
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect1 = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item);
            return purchaseDetailEntity;
        }).filter(item -> {
            return item.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode();
        }).collect(Collectors.toList());

        List<PurchaseDetailEntity> collect = collect1
                .stream()
                .map(item -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(item.getId());
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        //修改跟新时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(purchaseId);
        this.updateById(purchaseEntity);
    }

    /**
     *
     * @param ids 采购单
     */
    @Override
    public void received(List<Long> ids) {
        // 1 确入 当前 的 采购单 是 新建 或者 已 分配
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item -> {
            return item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
        }).map(item->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());//设置状态
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        // 2 改变 采购项 的 状态
        this.updateBatchById(collect);
        // 3 改变 采购单的采购项
        collect.forEach(item->{
            List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect1 = purchaseDetailEntityList.stream().map(entity -> {
                PurchaseDetailEntity e = new PurchaseDetailEntity();
                e.setId(entity.getId());
                e.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());//正在 采购
                return e;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseDoneVo.getId());
        purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISHED.getCode());//采购单已完成
        this.updateById(purchaseEntity);

        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> update = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if(item.getStatus()==WareConstant.PurchaseDetailStatusEnum.HASHERROR.getCode()){
                flag  = false;  //改变 采购单 状态--------有一个 采购项 失败 则 失败
                purchaseDetailEntity.setStatus(item.getStatus());
            }else{
                purchaseDetailEntity.setStatus(item.getStatus());
                //采购成功的 入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity.getWareId(),detailEntity.getSkuId(),detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            update.add(purchaseDetailEntity);
        }
        //改变采购项 状态
        this.purchaseDetailService.updateBatchById(update);
        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(purchaseDoneVo.getId());
        entity.setStatus(flag==true?WareConstant.PurchaseStatusEnum.FINISHED.getCode():WareConstant.PurchaseStatusEnum.HASHERROR.getCode());
        entity.setUpdateTime(new Date());
        this.updateById(entity);
        //将成功采购 的 进行入库
    }

}