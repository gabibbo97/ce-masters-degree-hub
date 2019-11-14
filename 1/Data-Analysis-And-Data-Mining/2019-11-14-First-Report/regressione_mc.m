%% Cleanup
clear % Clear variables
clc % Clean console
close all % Close all plots

%% Original function (ground truth)
num_true = 10000;
x_true = linspace(0,1,num_true)'; % Generation of the X axis
y_true = groundTruth(x_true);

sigma = 0.1; % Noise strength
num_samples = 9; % Number of points to sample from the function
p = num_samples - 1; % Degree of the approximating polynomial (should be between 1 and num_samples - 1)

num_monte_carlo = 1000;
err_monte_carlo = zeros(num_monte_carlo, p);

for mc = 1:num_monte_carlo

    % Sampling from ground truth
    x_sampling = rand(num_samples, 1); % Take num_samples points on the x-axis
    y_sampling = sampleWithGaussianNoise(x_sampling, sigma); % Sample and add gaussian noise

    learn_X = zeros(num_samples, p+1); % Matrix initialization
    for i = 1:p+1
        learn_X(:,i) = x_sampling .^ (i - 1); % Calculation of x^i for all xs
    end

    forward_X = zeros(num_true, p+1);
    for i = 1:p+1
        forward_X(:,i) = x_true .^ (i - 1);
    end

    for current_p = 0:p
        % Learning phase
        x_current = learn_X(:,1:current_p+1); % Rescale matrix
        c_current = (x_current' * x_current) \ (x_current' * y_sampling); % Recalculate coefficients

        y_current = forward_X(:,1:current_p+1) * c_current;
        average_error = mean(abs(y_current - y_true));

        err_monte_carlo(mc, current_p + 1) = average_error;

        y_samples = x_current * c_current;
        average_error_on_sample = mean(abs(y_samples - y_sampling));
    end

end

%% Print results
for i = 0:p
    errors = err_monte_carlo(:, i+1);
    error_on_p = mean(errors);
    fprintf('p = %d, error = %.02e\n', i, error_on_p);
end

%% Definitions
function y = groundTruth(x)
    y = x.^2;
end
function y = sampleWithGaussianNoise(x, sigma)
    y = groundTruth(x) + sigma * randn(size(x));
end