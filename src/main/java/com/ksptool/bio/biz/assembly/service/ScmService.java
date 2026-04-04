package com.ksptool.bio.biz.assembly.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.core.common.AppRegistry;
import com.ksptool.bio.biz.core.service.RegistrySdk;
import com.ksptool.bio.biz.assembly.model.scm.ScmPo;
import com.ksptool.bio.biz.assembly.model.scm.dto.AddScmDto;
import com.ksptool.bio.biz.assembly.model.scm.dto.EditScmDto;
import com.ksptool.bio.biz.assembly.model.scm.dto.GetScmListDto;
import com.ksptool.bio.biz.assembly.model.scm.vo.GetScmDetailsVo;
import com.ksptool.bio.biz.assembly.model.scm.vo.GetScmListVo;
import com.ksptool.bio.biz.assembly.repository.ScmRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.http.HttpConnection;
import org.eclipse.jgit.transport.http.HttpConnectionFactory;
import org.eclipse.jgit.transport.http.JDKHttpConnectionFactory;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Collection;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Slf4j
@Service
public class ScmService {

    @Autowired
    private ScmRepository repository;

    @Autowired
    private RegistrySdk registrySdk;

    /**
     * 获取SCM列表
     *
     * @param dto 查询条件
     * @return SCM列表
     */
    public PageResult<GetScmListVo> getScmList(GetScmListDto dto) {
        ScmPo query = new ScmPo();
        assign(dto, query);

        Page<ScmPo> page = repository.getScmList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetScmListVo> vos = as(page.getContent(), GetScmListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增SCM
     *
     * @param dto 新增数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void addScm(AddScmDto dto) throws BizException {

        //校验名称是否唯一
        if (repository.countByName(dto.getName()) > 0) {
            throw new BizException("名称已存在:[" + dto.getName() + "]");
        }

        ScmPo insertPo = as(dto, ScmPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑SCM
     *
     * @param dto 编辑数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void editScm(EditScmDto dto) throws BizException {
        ScmPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        //校验名称是否唯一
        if (repository.countByNameExcludeId(dto.getName(), updatePo.getId()) > 0) {
            throw new BizException("名称已存在:[" + dto.getName() + "]");
        }

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取SCM详情
     *
     * @param dto 查询条件
     * @return
     */
    public GetScmDetailsVo getScmDetails(CommonIdDto dto) throws BizException {
        ScmPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetScmDetailsVo.class);
    }

    /**
     * 删除SCM
     *
     * @param dto 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeScm(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

    /**
     * 测试SCM连接
     *
     * @param dto 测试条件
     */
    public Result<String> testScmConnection(CommonIdDto dto) throws BizException {
        ScmPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("测试连接失败,数据不存在或无权限访问."));

        //从注册表获取代理配置 FG_PROXY_ENABLE=1时启用代理
        var proxyEnable = registrySdk.getInt(AppRegistry.FG_PROXY_ENABLE.getFullKey(), 0);
        var proxyHost = registrySdk.getString(AppRegistry.FG_PROXY_HOST.getFullKey(), "127.0.0.1");
        int proxyPort = registrySdk.getInt(AppRegistry.FG_PROXY_PORT.getFullKey(), 8080);
        var proxyUsername = registrySdk.getString(AppRegistry.FG_PROXY_USERNAME.getFullKey(), "?");
        var proxyPassword = registrySdk.getString(AppRegistry.FG_PROXY_PASSWORD.getFullKey(), "?");

        try {

            var startTime = System.currentTimeMillis();

            LsRemoteCommand lrc = Git.lsRemoteRepository()
                    .setRemote(po.getScmUrl())
                    .setTimeout(10); // 设置超时

            boolean useProxy = proxyEnable == 1;

            // 根据不同的认证方式进行配置
            if (po.getScmAuthKind() == 1) { //账号密码/Token
                lrc.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(po.getScmUsername(), po.getScmPassword())
                );
                if (useProxy) {
                    setupHttpProxy(lrc, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            //SSH KEY
            if (po.getScmAuthKind() == 2) {
                setupSshAuthentication(lrc, po.getScmPk(), useProxy ? proxyHost : null, useProxy ? proxyPort : -1, proxyUsername, proxyPassword);
            }

            //PAT
            if (po.getScmAuthKind() == 3) {
                lrc.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(po.getScmUsername(), po.getScmPassword())
                );
                if (useProxy) {
                    setupHttpProxy(lrc, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            //公开
            if (po.getScmAuthKind() == 0) {
                lrc.setCredentialsProvider(null);
                if (useProxy) {
                    setupHttpProxy(lrc, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            //执行请求获取远程分支
            Collection<Ref> refs = lrc.call();

            //校验远程分支是否存在
            if (refs.isEmpty()) {
                throw new BizException("连接成功,但远程分支为空");
            }

            // 兼容处理：用户可能输入 "main" 或 "refs/heads/main"
            String targetBranch = po.getScmBranch();
            String fullBranchPath = targetBranch.startsWith("refs/") ? targetBranch : "refs/heads/" + targetBranch;

            boolean branchExists = refs.stream()
                    .anyMatch(ref -> ref.getName().equalsIgnoreCase(fullBranchPath));

            if (!branchExists) {
                throw new BizException("连接成功,但分支[" + po.getScmBranch() + "]不存在");
            }

            //测试成功
            return Result.success("测试成功 耗时: " + (System.currentTimeMillis() - startTime) + "ms", null);

        } catch (GitAPIException e) {
            throw new BizException("Git 连接失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BizException("系统异常: " + e.getMessage());
        }

    }

    /**
     * 为 HTTP/HTTPS 连接配置代理
     */
    private void setupHttpProxy(TransportCommand<?, ?> command, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
        log.info("当前HTTP已启用代理配置: proxyHost: {}, proxyPort: {}", proxyHost, proxyPort);
        HttpConnectionFactory factory = new JDKHttpConnectionFactory() {
            @Override
            public HttpConnection create(URL url) throws IOException {
                return create(url, null);
            }

            @Override
            public HttpConnection create(URL url, Proxy proxy) throws IOException {
                // 强制通过配置中的代理访问远程仓库
                Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                HttpConnection conn = super.create(url, httpProxy);
                // 若代理需要认证
                if (!"?".equals(proxyUsername) && !"?".equals(proxyPassword)) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            if (getRequestorType() == RequestorType.PROXY) {
                                return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                            }
                            return super.getPasswordAuthentication();
                        }
                    });
                }
                return conn;
            }
        };

        command.setTransportConfigCallback(transport -> {
            if (transport instanceof TransportHttp) {
                ((TransportHttp) transport).setHttpConnectionFactory(factory);
            }
        });
    }

    /**
     * 为 SSH 连接配置认证，可选启用 HTTP 代理穿透
     */
    private void setupSshAuthentication(TransportCommand<?, ?> command, String privateKey, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
        log.info("当前SSH已启用代理配置: proxyHost: {}, proxyPort: {}", proxyHost, proxyPort);
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
                if (proxyHost != null && proxyPort > 0) {
                    // SSH 请求通过 HTTP 代理隧道转发
                    ProxyHTTP proxy = new ProxyHTTP(proxyHost, proxyPort);
                    if (!"?".equals(proxyUsername) && !"?".equals(proxyPassword)) {
                        proxy.setUserPasswd(proxyUsername, proxyPassword);
                    }
                    session.setProxy(proxy);
                }
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch jsch = super.createDefaultJSch(fs);
                jsch.removeAllIdentity();
                jsch.addIdentity("git-ssh-key", privateKey.getBytes(), null, null);
                return jsch;
            }
        };

        command.setTransportConfigCallback(transport -> {
            if (transport instanceof SshTransport) {
                ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
            }
        });
    }


    /**
     * 从SCM拉取代码到本地目录
     * 若目录不存在则执行 clone，否则执行 pull
     *
     * @param scmPo    SCM配置
     * @param basePath 本地工作目录
     * @throws BizException 业务异常
     */
    public void pullFromScm(ScmPo scmPo, String basePath) throws BizException {
        var proxyEnable = registrySdk.getInt(AppRegistry.FG_PROXY_ENABLE.getFullKey(), 0);
        var proxyHost = registrySdk.getString(AppRegistry.FG_PROXY_HOST.getFullKey(), "127.0.0.1");
        int proxyPort = registrySdk.getInt(AppRegistry.FG_PROXY_PORT.getFullKey(), 8080);
        var proxyUsername = registrySdk.getString(AppRegistry.FG_PROXY_USERNAME.getFullKey(), "?");
        var proxyPassword = registrySdk.getString(AppRegistry.FG_PROXY_PASSWORD.getFullKey(), "?");
        boolean useProxy = proxyEnable == 1;

        File localDir = new File(basePath);

        try {
            if (localDir.exists() && new File(localDir, ".git").exists()) {
                // 已存在仓库时直接拉取远程分支
                log.info("pullFromScm: pull 目录={}", basePath);
                try (Git git = Git.open(localDir)) {
                    // 丢弃本地所有未提交变更，避免 pull 时产生冲突
                    git.checkout().setAllPaths(true).call();
                    git.clean().setForce(true).setCleanDirectories(true).call();

                    var pullCmd = git.pull()
                            .setRemoteBranchName(scmPo.getScmBranch())
                            .setTimeout(30);

                    // 公开仓库不带凭证，仅按需挂代理
                    if (scmPo.getScmAuthKind() == 0) {
                        pullCmd.setCredentialsProvider(null);
                        if (useProxy) {
                            setupHttpProxy(pullCmd, proxyHost, proxyPort, proxyUsername, proxyPassword);
                        }
                    }

                    // 账号密码和 PAT 统一走 HTTP 凭证
                    if (scmPo.getScmAuthKind() == 1 || scmPo.getScmAuthKind() == 3) {
                        pullCmd.setCredentialsProvider(
                                new UsernamePasswordCredentialsProvider(scmPo.getScmUsername(), scmPo.getScmPassword()));
                        if (useProxy) {
                            setupHttpProxy(pullCmd, proxyHost, proxyPort, proxyUsername, proxyPassword);
                        }
                    }

                    if (scmPo.getScmAuthKind() == 2) {
                        setupSshAuthentication(pullCmd, scmPo.getScmPk(),
                                useProxy ? proxyHost : null, useProxy ? proxyPort : -1,
                                proxyUsername, proxyPassword);
                    }

                    pullCmd.call();
                }
                return;
            }

            // 本地目录尚未初始化仓库时执行 clone
            log.info("pullFromScm: clone 目录={} url={}", basePath, scmPo.getScmUrl());
            var cloneCmd = Git.cloneRepository()
                    .setURI(scmPo.getScmUrl())
                    .setDirectory(localDir)
                    .setBranch(scmPo.getScmBranch())
                    .setTimeout(30);

            // 公开仓库不带凭证，仅按需挂代理
            if (scmPo.getScmAuthKind() == 0) {
                cloneCmd.setCredentialsProvider(null);
                if (useProxy) {
                    setupHttpProxy(cloneCmd, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            // 账号密码和 PAT 统一走 HTTP 凭证
            if (scmPo.getScmAuthKind() == 1 || scmPo.getScmAuthKind() == 3) {
                cloneCmd.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(scmPo.getScmUsername(), scmPo.getScmPassword()));
                if (useProxy) {
                    setupHttpProxy(cloneCmd, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            if (scmPo.getScmAuthKind() == 2) {
                setupSshAuthentication(cloneCmd, scmPo.getScmPk(),
                        useProxy ? proxyHost : null, useProxy ? proxyPort : -1,
                        proxyUsername, proxyPassword);
            }

            try (Git ignored = cloneCmd.call()) {
                // clone 完成后 Git 实例可关闭
            }
        } catch (GitAPIException e) {
            throw new BizException("Git 操作失败: " + e.getMessage());
        } catch (IOException e) {
            throw new BizException("本地目录访问失败: " + e.getMessage());
        }
    }

    /**
     * 将本地目录推送到SCM(这会自动add并commit并push)
     *
     * @param scmPo    SCM配置
     * @param basePath 本地工作目录
     * @throws BizException 业务异常
     */
    public void pushToScm(ScmPo scmPo, String basePath) throws BizException {
        var proxyEnable = registrySdk.getInt(AppRegistry.FG_PROXY_ENABLE.getFullKey(), 0);
        var proxyHost = registrySdk.getString(AppRegistry.FG_PROXY_HOST.getFullKey(), "127.0.0.1");
        int proxyPort = registrySdk.getInt(AppRegistry.FG_PROXY_PORT.getFullKey(), 8080);
        var proxyUsername = registrySdk.getString(AppRegistry.FG_PROXY_USERNAME.getFullKey(), "?");
        var proxyPassword = registrySdk.getString(AppRegistry.FG_PROXY_PASSWORD.getFullKey(), "?");
        boolean useProxy = proxyEnable == 1;

        File localDir = new File(basePath);

        if (!localDir.exists() || !new File(localDir, ".git").exists()) {
            throw new BizException("本地目录不存在或不是 Git 仓库: " + basePath);
        }

        try (Git git = Git.open(localDir)) {

            // 确保 remote origin 指向配置中的 URL
            StoredConfig config = git.getRepository().getConfig();
            config.setString("remote", "origin", "url", scmPo.getScmUrl());
            config.save();

            // 将工作区所有变更加入暂存区
            git.add().addFilepattern(".").call();

            // 若无可提交内容则跳过 commit 和 push
            var status = git.status().call();
            if (status.isClean()) {
                log.info("pushToScm: 工作区无变更，跳过提交 目录={}", basePath);
                return;
            }

            git.commit()
                    .setMessage("BioCode代码生成器自动提交")
                    .call();

            log.info("pushToScm: push 目录={} url={}", basePath, scmPo.getScmUrl());
            var pushCmd = git.push()
                    .setRemote("origin")
                    .setRefSpecs(new RefSpec("refs/heads/" + scmPo.getScmBranch() + ":refs/heads/" + scmPo.getScmBranch()))
                    .setTimeout(30);

            // 公开仓库不带凭证，仅按需挂代理
            if (scmPo.getScmAuthKind() == 0) {
                pushCmd.setCredentialsProvider(null);
                if (useProxy) {
                    setupHttpProxy(pushCmd, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            // 账号密码和 PAT 统一走 HTTP 凭证
            if (scmPo.getScmAuthKind() == 1 || scmPo.getScmAuthKind() == 3) {
                pushCmd.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(scmPo.getScmUsername(), scmPo.getScmPassword()));
                if (useProxy) {
                    setupHttpProxy(pushCmd, proxyHost, proxyPort, proxyUsername, proxyPassword);
                }
            }

            if (scmPo.getScmAuthKind() == 2) {
                setupSshAuthentication(pushCmd, scmPo.getScmPk(),
                        useProxy ? proxyHost : null, useProxy ? proxyPort : -1,
                        proxyUsername, proxyPassword);
            }

            pushCmd.call();

        } catch (GitAPIException e) {
            throw new BizException("Git 操作失败: " + e.getMessage());
        } catch (IOException e) {
            throw new BizException("本地目录访问失败: " + e.getMessage());
        }
    }


}