clear
close all
clc
 
%% True Function
nt = 1000;
xt = linspace(0,1,nt)';
yt = xt.^2;
 
%%
sigma = .1;
vn = [2,4,8,10,20,30];
nmc = 100;
vlambda = logspace(-4,4,9);
pmax = max(vn);
XTrue = zeros(nt,pmax+1);
for p = 0:pmax
    XTrue(:,p+1) = xt.^p;
end
err_t = zeros(length(vlambda),length(vn));
for mc = 1:nmc
    x = rand(max(vn),1);
    y = x.^2+sigma*randn(size(x));
    X = zeros(max(vn),pmax+1);
    for p = 0:pmax
        X(:,p+1) = x.^p;
    end
    in = 0;
    for n = vn
        in = in + 1;
        ilambda = 0;
        for lambda = vlambda
            ilambda = ilambda + 1;
            XT = X(1:n,:);
            c = (XT'*XT+lambda*eye(size(XT,2)))\(XT'*y(1:n));
            yp = XTrue(:,1:p+1)*c;
            err = mean(abs(yt-yp));
            err_t(ilambda,in) = err_t(ilambda,in) + err;
        end
    end
end
err_t = err_t / nmc;
 
%% Print Results
for i = 1:size(err_t,1)
    for j = 1:size(err_t,2)
        fprintf('%.2e ',err_t(i,j));
    end
    fprintf('\n');
end

imagesc(err_t)