clear
close all
clc

%% True function
nt = 1000;
xt = linspace(0,1,nt)';
yt = xt.^2;

%% Plot
figure, hold on, box on, grid on
plot(xt, yt);

%% Sampling
sigma = .1;
n = 40;
x = rand(n,1);
y = x .^ 2 + sigma * randn(size(x));

%% Plot
plot(x,y,'ob');

%% Forward phase
lambda = 1;
p = n-1;

X = zeros(n, p+1);
for i = 0:p
    X(:,i+1) = x .^ i;
end

c = (X' * X + lambda * eye(size(X, 2))) \ X' * y;

X = zeros(nt, p+1);
for i = 0:p
    X(:,i+1) = xt .^ i;
end

%% Compute learned function
yp = X * c;

%% Plot
plot(xt, yp, 'r')
