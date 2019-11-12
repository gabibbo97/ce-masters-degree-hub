clear
close all
clc

%% True function
nt = 1000;
xt = linspace(0,1,nt)';
yt = xt.^2;

%%
sigma = .1; % power of the noise
vn = [2,4,8,10,20,30]; % points to sample
nmc = 10000; %number of samples
vp = 0:4; %polynomial degree array
XTrue = zeros(nt, max(vp)+1);
err_t= zeros(length(vp), length(vn)); %average error
for p = 0:max(vp)
    XTrue(:,p+1) = xt.^p; 
end

X = zeros(nt, max(vp)+1);
for p = 0:max(vp)
    X(:,p+1) = xt.^p; 
end

for mc = 1:nmc
    x = rand(max(vn),1);
    y = x.^2 + sigma*randn(size(x));
    in = 0;
    for n = vn
        in = in +1;

        ip = 0;
        for p = vp
            ip = ip+1;
            XT = X(1:n,1:p+1);
            c = (XT'*XT)\(XT'*y(1:n));
            yp = XTrue(:,1:p+1)*c;
            err = mean(abs(yt-yp));
            err_t(ip,in) = err_t(ip,in) + err;
        end
    end
end
err_t = err_t / nmc;

%% Print result

for i = 1:size(err_t,1)
    fprintf('[ p = %d ] ', i - 1);
    for j = 1:size(err_t, 2)
        fprintf('%.1e ', err_t(i,j));
    end
    fprintf('\n');
end