--------------------------------------------------------------------------------
-- Company: LAAS 
-- Engineer: B. VNADEPORTAELE
-- 
--------------------------------------------------------------------------------
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--USE ieee.numeric_std.ALL;
 
ENTITY tb_uat IS
END tb_uat;
 
ARCHITECTURE behavior OF tb_uat IS 
 
    -- Component Declaration for the Unit Under Test (UUT)
 component uat
port (
------------------------------pragma_vhdl_entity-----------------------------------------------------------
 
D : in std_logic_vector(7 downto 0);
TX : out std_logic;
BaudRATE_SELECTOR   : in std_logic_vector(1 downto 0);
iq_cpt : buffer std_logic_vector(3 downto 0);
iq_reg_decal :buffer std_logic_vector(10 downto 0);
--------------------------end of pragma_vhdl_entity--------------------------------------------------------
		CK              : in     std_logic;
		ARAZB           : in     std_logic;
		STATE_NUMBER    : out    std_logic_vector( 0 downto 0);
		WRB             : in     std_logic;
		PRET            : out    std_logic);
end component;
------------------  OUTPUTS FOR CURRENT STATE VISUALIZATION ------------
--	state_number <= "0" when ( current_state = state_0)
--                   else "1" when ( current_state = state_1)
--                   else "1";   -- coding for error

-- pragma by hand 
signal D :  std_logic_vector(7 downto 0):="01010101";
signal TX :std_logic;
signal BaudRATE_SELECTOR   : std_logic_vector(1 downto 0):="00";
signal iq_cpt : std_logic_vector(3 downto 0);
signal iq_reg_decal : std_logic_vector(10 downto 0);

 --inputs
   signal ck : std_logic := '0';
   signal ARAZB : std_logic := '0';
   signal WRB : std_logic := '0';
  --outputs
   signal STATE_NUMBER:  std_logic_vector( 0 downto 0);
   signal PRET:  std_logic;
 
   constant ck_period : time := 100 ns;
 
	
BEGIN
  
	-- Instantiate the Unit Under Test (UUT)
	
	uat_u0 : uat
port map(
		CK => CK,
		ARAZB => ARAZB,
		D=>D,
		TX=>TX,
		baudrate_selector=>baudrate_selector,
		iq_cpt =>iq_cpt ,
		iq_reg_decal => iq_reg_decal ,
		STATE_NUMBER =>  STATE_NUMBER,
		WRB => WRB,
		PRET => PRET);
		 
 
 

   -- Clock process definitions
   ck_process :process
   begin
		ck <= '0';
		wait for ck_period/2;
		ck <= '1';
		wait for ck_period/2;
   end process;
 

   -- Stimulus process
   stim_proc: process
--(ck,resetn)
   begin		

 
	-- hold reset state for 100 ns.
	ARAZB<='0';
	wait for 100 ns;	
	ARAZB<='1';
	wait for ck_period*2;
	-- insert stimulus here 
	
---------------------------------------	
	wait until (ck'event and ck='0' );
	BaudRATE_SELECTOR<="00";
   D<="01010101";		
	WRB<='1';
	wait for ck_period;
	WRB<='0';
	wait for ck_period*20;
---------------------------------------	
	wait until (ck'event and ck='0' );
	BaudRATE_SELECTOR<="01";
   D<="01010101";		 
	WRB<='1';
	wait for ck_period;
	WRB<='0';
	wait for ck_period*40;
---------------------------------------		
	wait until (ck'event and ck='0' );
	BaudRATE_SELECTOR<="10";
   D<="11100101";		 
	WRB<='1';
	wait for ck_period;
	WRB<='0';
	wait for ck_period*80;
---------------------------------------		
		 		 	
		 wait;
   end process;

END;
