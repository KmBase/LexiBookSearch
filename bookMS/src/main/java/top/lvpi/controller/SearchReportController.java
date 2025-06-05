package top.lvpi.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import top.lvpi.common.BusinessException;
import top.lvpi.common.ErrorCode;
import top.lvpi.common.BaseResponse;
import top.lvpi.model.entity.Note;
import top.lvpi.model.entity.SearchReport;
import top.lvpi.service.SearchReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.stp.StpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/report")
@Tag(name = "检索报告管理", description = "检索报告相关接口")
@Slf4j
public class SearchReportController {

    @Autowired
    private SearchReportService reportService;

    @Operation(summary = "创建检索报告")
    @PostMapping("/create")
    @SaCheckLogin
    public BaseResponse<Boolean> createReport(@RequestBody SearchReport report) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        report.setUserId(userId);
        boolean result = reportService.createReport(report);
        return BaseResponse.success(result);
    }

    @Operation(summary = "更新检索报告")
    @PutMapping("/update")
    @SaCheckLogin
    public BaseResponse<Boolean> updateReport(@RequestBody SearchReport report) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        // 确保用户只能更新自己的报告
        SearchReport existingReport = reportService.getReportById(report.getId());
        if (existingReport == null || !existingReport.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权更新此报告");
        }
        // 防止前端篡改用户ID，强制设置为当前用户ID
        report.setUserId(userId);
        boolean result = reportService.updateReport(report);
        return BaseResponse.success(result);
    }

    @Operation(summary = "删除检索报告")
    @DeleteMapping("/delete/{id}")
    @SaCheckLogin
    public BaseResponse<Boolean> deleteReport(@PathVariable Long id) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        // 验证报告归属
        SearchReport report = reportService.getReportById(id);
        if (report == null || !report.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权删除此报告");
        }
        boolean result = reportService.deleteReport(id);
        return BaseResponse.success(result);
    }

    @Operation(summary = "获取检索报告详情")
    @GetMapping("/get/{id}")
    @SaCheckLogin
    public BaseResponse<SearchReport> getReportById(@PathVariable Long id) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        SearchReport report = reportService.getReportById(id);
        // 验证用户权限
        if (report == null || !report.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权访问此报告");
        }
        return BaseResponse.success(report);
    }

    @Operation(summary = "分页查询检索报告列表")
    @GetMapping("/list")
    @SaCheckLogin
    public BaseResponse<IPage<SearchReport>> listReports(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        IPage<SearchReport> page = reportService.listReports(current, size, keyword, userId);
        return BaseResponse.success(page);
    }

    @Operation(summary = "获取报告关联的笔记列表")
    @GetMapping("/{reportId}/notes")
    @SaCheckLogin
    public BaseResponse<List<Note>> getReportNotes(@PathVariable Long reportId) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        // 验证报告归属
        SearchReport report = reportService.getReportById(reportId);
        if (report == null || !report.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权访问此报告");
        }
        List<Note> notes = reportService.getReportNotes(reportId);
        return BaseResponse.success(notes);
    }

    @Operation(summary = "导出报告为PDF")
    @GetMapping("/{reportId}/export/pdf")
    @SaCheckLogin
    public ResponseEntity<byte[]> exportReportToPdf(@PathVariable Long reportId) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        // 验证报告归属
        SearchReport report = reportService.getReportById(reportId);
        if (report == null || !report.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权导出此报告");
        }
        
        byte[] pdfData = reportService.exportReportToPdf(reportId);
        String filename = report.getTitle() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        try {
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            headers.add("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败", e);
            headers.setContentDispositionFormData("attachment", "report.pdf");
        }
        headers.setContentLength(pdfData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    @Operation(summary = "导出报告为Markdown")
    @GetMapping("/{reportId}/export/markdown")
    @SaCheckLogin
    public ResponseEntity<String> exportReportToMarkdown(@PathVariable Long reportId) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        // 验证报告归属
        SearchReport report = reportService.getReportById(reportId);
        if (report == null || !report.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权导出此报告");
        }
        
        String markdown = reportService.exportReportToMarkdown(reportId);
        String filename = report.getTitle() + ".md";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_MARKDOWN);
        try {
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            headers.add("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败", e);
            headers.setContentDispositionFormData("attachment", "report.md");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(markdown);
    }

    @Operation(summary = "生成报告分享链接")
    @PostMapping("/{reportId}/share")
    @SaCheckRole("admin")
    public BaseResponse<String> generateShareLink(@PathVariable Long reportId) {
        String shareLink = reportService.generateShareLink(reportId);
        return BaseResponse.success(shareLink);
    }

    @Operation(summary = "获取用户的报告列表")
    @GetMapping("/user/{userId}")
    @SaCheckLogin
    public BaseResponse<List<SearchReport>> getUserReports(@PathVariable Long userId) {
        Long currentUserId = Long.parseLong(StpUtil.getLoginId().toString());
        // 验证用户权限
        if (!currentUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权访问此用户的报告");
        }
        List<SearchReport> reports = reportService.getUserReports(userId);
        return BaseResponse.success(reports);
    }

    @Operation(summary = "获取分享的报告")
    @GetMapping("/share/{fileName}")
    public ResponseEntity<byte[]> getSharedReport(@PathVariable String fileName) {
        byte[] pdfData = reportService.getSharedReport(fileName);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfData.length);
        headers.add("Content-Disposition", "inline; filename=\"shared_report.pdf\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    @Operation(summary = "获取当前用户的检索菜单报告列表")
    @GetMapping("/search-subjects")
    @SaCheckLogin
    public BaseResponse<List<SearchReport>> getSearchSubjectReports() {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        List<SearchReport> reports = reportService.getSearchSubjectReports(userId);
        return BaseResponse.success(reports);
    }
}
