<template>
  <a-modal v-model:visible="visible"
           modal-class="modal-form-large"
           title-align="start"
           title="分配菜单"
           width="80%"
           :top="40"
           :body-style="{ padding: '16px 16px 0 16px', 'margin-bottom': '16px' }"
           :align-center="false"
           :draggable="true"
           :mask-closable="false"
           :unmount-on-close="true"
           :ok-button-props="{ disabled: loading }"
           :cancel-button-props="{ disabled: loading }"
           :on-before-ok="handlerOk"
           @close="handleClose">
    <div class="role-menu-wrapper">
      <a-spin class="full" :loading="loading">
        <a-alert class="usn mb8">
          <span>{{ roleRecord.name }} {{ roleRecord.code }}</span>
          <span class="mx8">-</span>
          <span>菜单分配后需要用户手动刷新页面才会生效</span>
        </a-alert>
        <div class="usn mb8">
          <a-space>
            <a-tag color="arcoblue">全选操作</a-tag>
            <!-- 全选操作 -->
            <template v-for="opt of quickGrantMenuOperator" :key="opt.name">
              <a-button size="mini" type="text" @click="() => { table.checkOrUncheckByFilter(opt.filter, true) }">
                {{ opt.name }}
              </a-button>
            </template>
          </a-space>
        </div>
        <div class="usn mb8">
          <a-space>
            <a-tag color="arcoblue">反选操作</a-tag>
            <!-- 反选操作 -->
            <template v-for="opt of quickGrantMenuOperator" :key="opt.name">
              <a-button size="mini" type="text" @click="() => { table.checkOrUncheckByFilter(opt.filter, false) }">
                {{ opt.name }}
              </a-button>
            </template>
          </a-space>
        </div>
        <!-- 菜单 -->
        <menu-grant-table ref="table" />
      </a-spin>
    </div>
  </a-modal>
</template>

<script lang="ts">
  export default {
    name: 'roleMenuGrantModal'
  };
</script>

<script lang="ts" setup>
  import type { RoleGrantMenuRequest, RoleQueryResponse } from '@/api/user/role';
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import useVisible from '@/hooks/visible';
  import { getRoleMenuId, grantRoleMenu } from '@/api/user/role';
  import { Message } from '@arco-design/web-vue';
  import { quickGrantMenuOperator } from '../types/const';
  import MenuGrantTable from '@/components/system/menu/grant-table/index.vue';

  const { visible, setVisible } = useVisible();
  const { loading, setLoading } = useLoading();

  const table = ref();
  const roleRecord = ref<RoleQueryResponse>({} as RoleQueryResponse);

  // 打开新增
  const open = async (record: any) => {
    renderRecord(record);
    setVisible(true);
    try {
      setLoading(true);
      // 获取角色菜单
      const { data: roleMenuIdList } = await getRoleMenuId(record.id);
      table.value.init(roleMenuIdList);
    } catch (e) {
    } finally {
      setLoading(false);
    }
  };

  // 渲染对象
  const renderRecord = (record: any) => {
    roleRecord.value = Object.assign({}, record);
  };

  defineExpose({ open });

  // 确定
  const handlerOk = async () => {
    setLoading(true);
    try {
      // 修改
      await grantRoleMenu({
        roleId: roleRecord.value.id,
        menuIdList: [...table.value.getValue()]
      } as RoleGrantMenuRequest);
      Message.success('分配成功');
      // 清空
      handlerClear();
    } catch (e) {
      return false;
    } finally {
      setLoading(false);
    }
  };

  // 关闭
  const handleClose = () => {
    handlerClear();
  };

  // 清空
  const handlerClear = () => {
    setLoading(false);
  };

</script>

<style lang="less" scoped>
  .role-menu-wrapper {
    width: 100%;
    max-height: calc(100vh - 230px);
  }
</style>
