<template>
  <div>
    <el-tree
      :data="menus"
      :props="defaultProps"
      show-checkbox
      node-key="catId"
      :expand-on-click-node="false"
      :default-expanded-keys="expandedKey"
      :draggable="true"
      :allow-drop="allowDrop"
      @node-drop="handleDrop"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
          <!--当前节点的名字-->
        <span>{{ node.label }}</span>
        <!--只有在一级和二级分类的时候可以depend-->
        <!--只有在底层分类的时候可以delete-->
        <span>
          <el-button v-if="node.level<=2" type="text" size="mini" @click="() => append(data)">Append</el-button>
          <el-button v-if="node.childNodes.length==0" type="text" size="mini" @click="() => remove(node, data)">Delete</el-button>
          <el-button type="text" size="mini" @click="() => edit(data)">Edit</el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog :title="title" :visible.sync="dialogVisible" width="30%" :close-on-click-modal="false">
          <!--这个对话框 是修改 打开的 还是 新增 打开的 -->
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
         <el-form-item label="图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="sumbitData">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      updateNode: [],//拖拽后 所有 要 修改的 节点 
      maxLevel: 0,//记录最大深度
      title: "",
      dialogType: "",  //这个对话框 是修改edit 打开的 还是 新增add 打开的 
      category: { name: "", parentCid: 0, catLevel: 0, showStatus: 1, sort: 0 ,catId:null,productUnit:"",icon:""},
      dialogVisible: false,
      menus: [],
      expandedKey: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
    };
  },
  methods: {
    sumbitData(){
      if(this.dialogType == "add"){
        this.addCategory();
      }
      if(this.dialogType == "edit"){
        this.editCategory();
      }
    },
    //触发拖拽事件------更新数据库
    handleDrop(draggingNode, dropNode, dropType, ev) {
      //我们需要的信息都可以 从 第二个参数获得
      //1，当前节点最新的父节点id
      console.log(dropType);
      let pCid = 0;
      let slbing = null;//记录该节点的 兄弟节点 用来 排序
      if(dropType == "before" || dropType == "after"){
          pCid = dropNode.parent.data.catId == undefined ? 0 : dropNode.parent.data.catId;
          slbing = dropNode.parent.childNodes;
      }else{
          pCid = dropNode.data.catId;
          slbing = dropNode.childNodes;
      }
      //2，当前最新节点的最新顺序
      //遍历  兄弟节点
      for(let i=0;i<slbing.length;i++){
        if(draggingNode.data.catId == slbing[i].data.catId){//如果遍历是当前拖拽的节点----》更新他的父id-->得到更新 节点的 全部信息
            let catLevel = draggingNode.levle;//当前节点拖拽的level
            if(catLevel != draggingNode.level){
                if(slbing[i].level != draggingNode.level){//当前节点的层数 发生变化
                  //修改当前节点的层级
                  catLevel = slbing[i].level;//当前节点的层数 和 兄弟节点一样
                  //修改他子节点的层级
                  this.updateChildNodeLevel(slbing[i]);
                } 
            }
            //继续修改子节点的层级---------递归修改

            this.updateNode.push({catId:slbing[i].data.catId,sort:i,patentCid:pCid});//自己 需要 改下 父节点
        }else{
            this.updateNode.push({catId:slbing[i].data.catId,sort:i});//兄弟 只需要 改  顺序
        }
      }
      //3,当前拖拽节点的最新层级 
      //发送post请求
      this.$http({
        url: this.$http.adornUrl("/product/category/update/sort"),
        method: "post",
        data: this.$http.adornData(this.updateNode, false),
      }).then((data) => {
        this.$message({
          type: "success",
          message: "菜单顺序修改成功",
        });
        //刷新出新的菜单
        this.getMenus();
        //设置需要默认展开的菜单
        this.dialogVisible = false;
        //设置需要默认展开的菜单
        this.expandedKey = [pCid];
      });
    },
    updateChildNodeLevel(node){
      if(node.childNodes.length>0){
        for(let i =0;i<node.childNodes.length;i++){
            var cNode = node.childNodes[i].data;//真正的数据
            this.updateNode.push({catId: cNode.catId, catLevel: node.childNodes[i].level});
            this.updateChildNodeLevel(node.childNodes[i]);//----------------递归修改
        }
      } 
    },
    //这里 要 求出 拖动节点 占据 多少个 深度  【当前节点深度= 最大深度 - 拖动节点深度 + 1】
    allowDrop(draggingNode, dropNode, type) {
      //被拖动的 当前 节点 以及 所在的父节点总层数 不能 超过 3
      //被拖动的 当前 节点
      this.countNodeLevel(draggingNode.data);
      let deep = this.maxLevel  - draggingNode.data.catLevel + 1 ;
      console.log("深度为：", deep);
      if(type == "inner"){//往里面拖动
        return deep + dropNode.level <=3;
      }else{//放在 组件的 前面 或者 后面
        return deep + dropNode.parent.level <= 3;
      }
    },
    countNodeLevel(node){
      // //找到所有子节点，求出最大深度
      if(node.children != null && node.children.length){
        for(let i = 0;i < node.children.length;i++){
          if(node.children[i].catLevel>this.maxLevel){
              this.maxLevel=node.children[i].catLevel;
          }
          this.countNodeLevel(node.children[i]);//递归查找 子节点
        }
      }
    },
    //添加三级分类
    addCategory() {
      console.log(this.category);
      //发送post请求
      this.$http({
        url: this.$http.adornUrl("/product/category/save"),
        method: "post",
        data: this.$http.adornData(this.category, false),
      }).then((data) => {
        this.$message({
          type: "success",
          message: "菜单添加成功",
        });
        //刷新出新的菜单
        this.getMenus();
        //设置需要默认展开的菜单
        this.dialogVisible = false;
        //设置需要默认展开的菜单
        this.expandedKey = [this.category.parentCid];
      });
    },
    //发送修改 的 Post 请求
    editCategory(){
      //准备数据---解构---发送部分数据
      var {catId,name,icon,productUnit} = this.category;
      this.$http({

        url: this.$http.adornUrl("/product/category/update"),
        method: "post",
        data: this.$http.adornData( {catId,name,icon,productUnit}, false),
      }).then((data) => {
        this.$message({
          type: "success",
          message: "菜单修改成功",
        });
        //刷新出新的菜单
        this.getMenus();
        //设置需要默认展开的菜单
        this.dialogVisible = false;
        //设置需要默认展开的菜单
        this.expandedKey = [this.category.parentCid];
      });
    },
    getMenus() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get",
      }).then((data) => {
        console.log("成功创建菜单数据。。。", data.data.page);
        this.menus = data.data.page;
      });
    }, 
    //修改菜单栏数据
    edit(data) {
      this.title ="修改菜单数据",
      this.dialogType = "edit";
      this.dialogVisible = true;
      //回显数据---------------------这里有bug，因为这里的 data 是 旧的数据，而 其他用户 可能修改了数据
      // this.category.name = data.name;
      // this.category.productCount = data.productCount;
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: "get",
      }).then((data) => {
        //请求成功----这个data是我们从服务器拿来的data
        console.log(data);
        this.category.name = data.data.category.name;
        this.category.icon = data.data.category.icon;
        this.category.productUnit = data.data.category.productUnit;
        this.category.parentCid = data.data.category.parentCid;//这里为了 在 后面 展开 父菜单的适合用到
      });
      //后台需要catId
      this.category.catId = data.catId;
    },
    append(data) {
      this.title ="添加菜单数据",
      this.dialogType = "add";
      this.dialogVisible = true;
      //这时候给parentCid和catLevel赋值
      this.category.catLevel = data.catLevel + 1;
      this.category.parentCid = data.catId;
      //下面是恢复数据
      this.category.name = "";
      this.sort = 0;
      this.showStatus = 1;
      this.catId = null;
      this.category.icon = "";
      this.category.productUnit = "";
    },
    remove(node, data) {
      var ids = [data.catId];
      this.$confirm(`是否删除【${data.name}】菜单`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          //确认删除 发送这个请求
          this.$http({
            url: this.$http.adornUrl("/product/category/delete"),
            method: "post",
            data: this.$http.adornData(ids, false),
          }).then((data) => {
            this.$message({
              type: "success",
              message: "删除成功!",
            });
            //刷新出新的菜单
            this.getMenus();
            //设置需要默认展开的菜单
            this.expandedKey = [node.parent.data.catId];
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
  },
  //   activated() {//如果页面有keep-alive缓存功能，这个函数就会出发，页面被激活就触发
  created() {
    this.getMenus();
  },
};
</script>