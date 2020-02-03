% basic initialization
max_node_count = 20;
sc = rtccurve( [[0,0,0] ; [0.1,0,10]] );
a_standard = rtccurve([0,1,0.67]);
a_foi_curr = a_standard;
a_foi_prev = a_standard; % meaningless for now but will do it later
a_fresh = a_standard;
a_old = a_standard;

delay_cum = 0;
delay_n_curr = 0;
fl_cum_delay_list = [];
fl_nodal_delay_list = [];

% 1st Node in for loop

a_cum_curr = rtcplus(a_foi_curr,a_fresh);
a_cum_curr = rtcplus(a_cum_curr,a_old);

delay_n_curr = rtch(a_cum_curr,sc);
delay_cum = delay_cum + delay_n_curr;

fl_cum_delay_list = [fl_cum_delay_list,delay_cum];
fl_nodal_delay_list =[fl_nodal_delay_list,delay_n_curr];


a_old = rtcmindeconv(a_fresh,sc); % this prepares a_old for its final thing
% 2nd Node in for loop and on 
for i = 1:(max_node_count-1)
    a_foi_curr = rtcmindeconv(a_foi_curr,sc); % this updates the current arrival_curve
    % until the very end nothing else will change so I don't need to update
    % the new and the old, just keep using a new and an old. Thus...
    a_cum_curr = rtcplus(a_foi_curr,a_fresh);
    a_cum_curr = rtcplus(a_cum_curr,a_old);
    
    delay_n_curr = rtch(a_cum_curr,sc);
    delay_cum = delay_cum + delay_n_curr;
    
    fl_cum_delay_list = [fl_cum_delay_list,delay_cum];
    fl_nodal_delay_list =[fl_nodal_delay_list,delay_n_curr];
    
end

%% Plotting
x = [1:1:20];
scatter(x,fl_cum_delay_list,'+');
legend('RTC TFA (FIFO Mul.)')
xlabel('Length of Tandem Network');
ylabel('Seconds')
grid on;

