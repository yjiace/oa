package cn.smallyoung.oa;

import cn.smallyoung.oa.entity.DocumentApproval;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.service.DocumentApprovalService;
import cn.smallyoung.oa.service.SysUserService;
import cn.smallyoung.oa.vo.DocumentApprovalVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @data 2020/11/21
 */
@Slf4j
@SpringBootTest(classes = OaApplication.class)
public class DocumentApprovalTest {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private DocumentApprovalService documentApprovalService;

    /**
     * 提交审批
     */
    @Test
    public void testSubmitForApproval(){
        DocumentApprovalVO documentApprovalVO = new DocumentApprovalVO();
        documentApprovalVO.setDocumentNumber("122333");
        documentApprovalVO.setSecurityClassification("smallyoung");
        documentApprovalVO.setUsername(sysUserService.findAll().stream().map(SysUser::getUsername).collect(Collectors.toList()));
        documentApprovalService.submitForApproval(documentApprovalVO);
    }

    /**
     * 拒绝审批
     */
    @Test
    public void testRejectedApproval(){
        DocumentApproval documentApproval = documentApprovalService.findOne(5L);
        documentApprovalService.rejectedApproval(documentApproval);
    }

    /**
     * 同意审批
     */
    @Test
    public void testCompletedApproval(){

    }
}
