%% Cleanup
clear % Clear variables
clc % Clean console
close all % Close all plots

%% Original function (ground truth)
num_true = 10000;
x_true = linspace(0,1,num_true)'; % Generation of the X axis
y_true = groundTruth(x_true);

%% Learning phase
lambdas = logspace(-3,1, 9);
lambdas(2) = lambdas(1);
lambdas(1) = 0; % Test lambda = 0

num_samples = 10; % Number of points to sample from the function
sigma = 0.05; % Noise strength

num_monte_carlo = 1000;
err_monte_carlo = zeros(num_monte_carlo, length(lambdas));

for mc = 1:num_monte_carlo
    ilambda = 1;
    
    x_sampling = rand(num_samples, 1); % Take num_samples points on the x-axis
    y_sampling = sampleWithGaussianNoise(x_sampling, sigma); % Sample and add gaussian noise
    
    for lambda = lambdas % Iterate over lambdas
        
        p = num_samples - 1; % Use maximum p
        X_learning = zeros(num_samples, p+1);
        for i = 0:p
            X_learning(:,i + 1) = x_sampling .^ i;
        end
        c = (X_learning' * X_learning + lambda * eye(p+1)) \ (X_learning'*y_sampling);

        % Forward
        X_forward = zeros(num_true, p+1);
        for i = 0:p
            X_forward(:,i + 1) = x_true .^ i;
        end
        Y_forward = X_forward * c;

        average_error = mean(abs(Y_forward - y_true));
        
        err_monte_carlo(mc, ilambda) = average_error;
        ilambda = ilambda + 1;
    end
end

%% Print results
for i = 1:length(lambdas)
    errors = err_monte_carlo(:, i);
    error_on_p = mean(errors);
    fprintf('lambda = %.02e, error = %.02e\n', lambdas(i), error_on_p);
end

%% Definitions
function y = groundTruth(x)
    y = x.^2;
end
function y = sampleWithGaussianNoise(x, sigma)
    y = groundTruth(x) + sigma * randn(size(x));
end