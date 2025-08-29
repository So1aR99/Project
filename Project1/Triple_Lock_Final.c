/*
  Triple_Lock_Final.c
 
  Created: 2025-08-25 오후 2:17:31
  Author: COMPUTER 
 
  A/D변환기를 이용해 가변저항값을 최대값(1024)로 맞추고 값이 맞으면 1번 LED가 점등된다.
  그 이후에 키패드를 이용해 비밀번호를 입력 후 비밀번호가 맞다면 2번 LED에 불이 2초간
  점등되는데 키패드를 이용해 5초안에 비밀번호를 입력하지 않으면 1번과 2번 LED가 함께 깜빡이며 초기화된다.(타이머 카운터 이용)
  시리얼통신을 통해 정해진 값을 입력받으면 3번째 LED가 켜지고 스탭모터(Step Motor)가 작동된다.
  만약 키패드를 이용해 입력한 비밀번호가 맞지 않으면 2번 LED가 여러번 반짝인다.
  순차적으로 비밀번호를 입력해야 한다. 만약 1,2번 단계를 진행하지 않고 3번 단계인 시리얼을 통해 비밀번호를 입력하려고 하면 접근이 불가능하다.
 
 
  *****트리플 락(Triple_Lock)*****
 
  1. A/D변환기를 이용 가변저항 1020이상일때 잠금해제, 1번 LED가 켜지고 가변저항값이 바뀌면 1번 LED가 다시꺼지고 잠김.
     -> LCD에 First Password 출력 후 가변저항 값을 맞추면 Access출력과 함께 1번 LED 점등.
 
  2. 1번 잠금해제 후 4자리 비밀번호를 입력받아 잠금해제, 2번 LED가 켜지고 만약 틀리면 실패 메시지와 함께 2번 LED가 깜빡거려 실패를 알리고,
     타이머/카운터를 이용해 5초안에 4자리 비밀번호를 모두 입력받지 않으면 타임아웃 메시지와 함께 1,2번 LED가 여러번 깜빡거려 빨리 입력할 수 있도록 알림.
     -> LCD에 Second Password 출력 후 키패드로 4자리 비밀번호를 맞추면 Access출력과 함께 1,2번 LED 모두 점등.
        만약 비밀번호가 틀리면 Fail 메시지 출력과 함께 5번 LED 꺼졌다 켜져서 알림.
        타이머/카운터를 이용해 5초안에 비밀번호를 입력하지 못하면 TIMEOUT 메시지와 함께 1,2번 LED 모두 깜빡거리며 알림.
 
  3. 시리얼 통신을 통해 정해진 문자('A')를 입력받으면 3번째 LED가 켜진다.
     -> 만약 1,2번 잠금을 해제하지 않고 시리얼통신을 진행할 경우 실행이 안되며, 순차적으로 진행하라는 메시지가 시리얼통신에 디스플레이 된다.
        그리고 LCD에 Final Password와 Type on the key가 출력되어 키보드로 정해진 비밀번호를 입력하면 3번 LED가 점등되고 LCD에는 Access가 출력이 되고,
        만약 비밀번호가 틀리면 3번 LED가 5번 깜빡거리며 Fail메시지가 LCD에 출력되고 시리얼통신에 Enter password again이란 문구가 출력되고 다시 입력할 
        수 있도록 LCD에 Final Password와 Type on the key가 출력된다.
        만약 1,2번 잠금을 해제하지 않은 상태에서 시리얼 통신을 통해 비밀번호를 입력하려고 한다면 시리얼통신에 Complete steps 1 and 2 first란 문구가
        출력되며 앞선 단계부터 먼저 완료하라는 문구가 출력되고 LCD에는 No Access를 출력한 뒤 처음부터 실행된다.
    
  4. 시리얼 통신을 통해 비밀번호가 입력되면 스탭모터가 구동되며, 구동되는 도중 첫 번째 비밀번호인 가변저항값을 바꾸면 모터 작동을 중지할 수 있다.
     -> 시리얼통신에는 Access란 문구가 출력되고 3초후에 Step Motor ON이 LCD에 출력되고 2초후에 Running 메시지와 함께 Step Motor가 작동되고, 만약 작동 중
        중단을 하고 싶은 경우 첫번째 비밀번호인 가변저항의 값을 바꾸면 LCD에 STOP메시지와 함께 LED가 모두 꺼지고 모터도 작동이 중지되며, 처음 단계로 다시 되돌아간다.
 
  ----------------------------------------------------------------------------------------------------
  
  1. 가변저항으로 1차 잠금 해제
     - 맞으면 LED1 ON과 LCD에 Access!!!! 출력
     - Access!!!! 출력 후 LCD에 Second Password 출력
     - 2행에 가변저항 값 출력
     - 1차 비밀번호 = 가변저항 값 1020이상으로 설정
 	
  2. 4자리 비밀번호 키패드 입력
     - 맞으면 LED2 ON과 LCD에 Access!!!! 출력
     - Access!!!! 출력 후 LCD에 Final Password
      Type on the key 출력
     - 틀리면 LED2 깜빡거리며 Fail!!!! 출력 후 5회 깜빡거리고 
      다시 Second Password 출력
	 - LCD에 경과시간 출력
     - 5초안에 4자리 비밀번호 미입력 시 LCD에
      TIMEOUT!!! 출력과 함께 1,2번 LED 빠르게 깜빡거리고
       다시 Second Password 출력
     - 2차 비밀번호 = '0105'으로 설정
 	
  3. 시리얼 통신을 통해 정해진 값 입력
     - 맞으면 LED3 ON과 LCD에 Access!!!! 출력
     - Access!!!! 출력 후 Step Motor ON 메시지 출력 후
       터미널에는 Clear!!! 출력
     - 틀리면 LED3 깜빡거리며 Fail!!!! 출력 후 5회 깜빡거리고
       다시 Final Password Type on the key 출력 후
       터미널에는 Enter password again!!!! 출력
     - 3차 비밀번호 = 'U'로 설정
  
  4. 모든 비밀번호가 입력 되었을 때 모터 구동
     - Step Motor ON 출력 후 2초 뒤 다음 행에
       Running!!!! 출력 후 시계방향으로 Step Motor
       무한 회전
     - 만약 중단하고 싶다면 첫번째 비밀번호인 가변저항값을
       바꾸면 Step Motor OFF 출력과 함께 Step Motor가 멈춤
     - 모터가 멈추고 LCD에는 다시 First Password 출력과 함께
       처음으로 돌아감
 
  5. 반드시 순서대로 구동
     - 만약 1,2번째 잠금을 해제하지 않고 터미널을 이용해 마지막
       비밀번호를 입력하려고 하면 LCD에는 No Access!! 출력되고
       터미널에는 Complete steps 1 and 2 first! 출력된 후
       처음 단계로 되돌아감
     - 처음 단계로 되돌아갈 땐 켜졌던 LED가 모두 꺼짐
	
  6. 사용한 하드웨어
     - 가변저항(ADC) : PF0
     - LED : PA0 ~ PA2
       ( PORTA |= 0x0n : 특정 비트만 ON
         PORTA &= 0x0n : 특정 비트만 OFF)
     - 키패드 : PD0 ~ PD7
       ( ROW0 ~ ROW3 : 행(입력) PD0 ~ PD3 
         COL0 ~ COL2 : 열(출력) PD4 ~ PD6 )
     - LCD : PC0 ~ PC7, PB0 ~ PB2(RS, RW, EN)
     - Step Motor : PG0 ~ PG3
 	
  7. 통신
     - USART(시리얼 통신)이용
     - 키보드로 입력받아 터미널 프로그램에서 문자열을 
       송수신해 그 값을 반환해 다시 터미널에 출력함.
 	  
  8. 타이머/인터럽트
     - Timer1 이용 5초 제한 타이머
     - Normal Mode
     - 분주비 1024
	 - 경과시간 LCD에 출력
 */

#define F_CPU 14745600UL
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>

#define LCD_WDATA	PORTC
#define LCD_WINST	PORTC
#define LCD_CTRL	PORTB
#define LCD_RS		0
#define LCD_RW		1
#define LCD_EN		2

typedef char Byte;

unsigned int read_adc(void);

void Port_Init(void);
void LCD_Data(Byte ch);
void LCD_Comm(Byte ch);
void LCD_CHAR(Byte c);
void LCD_STR(Byte *str);
void LCD_pos(Byte col, Byte row);
void LCD_Clear(void);
void LCD_Init(void);

void keypad_Init(void);
char keypad_getkey(void);
void clear_input(void);
void Init_USART(void);

void USART0_tx(unsigned char data);
unsigned char USART0_rx(void);
void USART0_str(unsigned char* str);
int USART_Available(void);

// 상태 변수
int adc_max = 0;												// ADC(가변저항) 최대값 ( 0:도달x, 1:도달 )
unsigned int prev_adc = 0;										// 이전 ADC값 저장
#define PASSWORD_LENGTH 4                                       // 비밀번호 길이
char password[PASSWORD_LENGTH] = {'0', '1', '0', '5'};          // 비밀번호 설정
char input_pass[PASSWORD_LENGTH];                               // 입력받은 비밀번호 저장
int input_index = 0;                                            // 입력된 숫자 저장
char Final_pass = 'U';                                          // 3번째 잠금 비밀번호(시리얼 통신)
int password_stage = 0;                                         // 비밀번호 입력 단계 ( 0:입력x, 1:입력 )
int timeout = 0;                                                // 타임아웃 여부 ( 0:발생x, 1:발생 )
int timer_start = 0;                                            // 타이머 시작 여부 ( 0:시작x, 1:시작

// 잠금 단계 관리 변수 추가
int first_lock_open = 0;                                        // 1번째 잠금 해제 상태 (ADC)
int second_lock_open = 0;                                       // 2번째 잠금 해제 상태 (키패드)
int third_lock_open = 0;                                        // 3번째 잠금 해제 상태

// 타이머 변수
volatile int t_cnt = 0, m_cnt = 0;
volatile int sec = 0;											// 초 단위 경과 변수

// 타이머1 오버플로우 인터럽트 설정
ISR(TIMER1_OVF_vect)
{
    TCNT1H = 0xf4;                                              // 타이머/카운터 초기값 재설정
    TCNT1L = 0xc0;                                              // 타이머/카운터 초기값 재설정
    t_cnt++;
    if(t_cnt >= 5) {                                            // 200[msec] 가 5번이 발생하면, 즉 1초가 지나면
        t_cnt = 0;
		if(sec > 5) sec = 0;									// 5초 경과하면 0으로 초기화
		sec++;													// 초 증가
        m_cnt++;
        if (m_cnt >= 5) {                                       // 1초가 5번이 발생, 즉 5초가 지나면
            timeout = 1;                                        // 타임아웃 발생
            TIMSK &= ~(1 << TOIE1);                             // 타이머 인터럽트 끄기
        }
    }
}

// ---------------------------- LCD 함수 ----------------------------
void LCD_Data(Byte ch)											// 문자 데이터 LCD에 출력
{
    LCD_CTRL |= (1 << LCD_RS);
    LCD_CTRL &= ~(1 << LCD_RW);
    LCD_CTRL |= (1 << LCD_EN);
    _delay_us(50);
    LCD_WDATA = ch;
    _delay_us(50);
    LCD_CTRL &= ~(1 << LCD_EN);
}

void LCD_Comm(Byte ch)											// 명령어를 LCD로 전송
{
    LCD_CTRL &= ~(1 << LCD_RS);
    LCD_CTRL &= ~(1 << LCD_RW);
    LCD_CTRL |= (1 << LCD_EN);
    _delay_us(50);
    LCD_WINST = ch;
    _delay_us(50);
    LCD_CTRL &= ~(1 << LCD_EN);
}

void LCD_CHAR(Byte c)											// 한 문자 출력
{
    LCD_Data(c);
    _delay_ms(1);
}

void LCD_STR(Byte *str)											// 문자열 출력
{
    while (*str != 0)
    {
        LCD_CHAR(*str);
        str++;
    }
}

void LCD_pos(Byte col, Byte row)								// 커서 위치 지정
{
    LCD_Comm(0x80 | (row + col * 0x40));
}

void LCD_Clear(void)											// LCD 전체 지우기
{
    LCD_Comm(0x01);
    _delay_ms(2);
}

void LCD_Init(void)												// LCD 초기 설정
{
    LCD_Comm(0x38);
	_delay_ms(2);
    LCD_Comm(0x0E);
	_delay_ms(2);
    LCD_Comm(0x06);
	_delay_ms(2);
    LCD_Clear();
}

// ---------------------------- USART 함수 ----------------------------
void Init_USART()												// 시리얼통신(USART) 초기 설정
{
    UCSR0A = 0x00;
    UCSR0B = (1 << RXEN0) | (1 << TXEN0);
    UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
    UBRR0H = 0;
    UBRR0L = 7;
}

void USART0_tx(unsigned char data)                              // 문자 1개 송신
{
    while(!(UCSR0A & (1 << UDRE0)));
    UDR0 = data;
}

unsigned char USART0_rx()                                       // 문자 1개 수신
{
    while(!(UCSR0A & (1 << RXC0)));
    return UDR0; 
}

void USART0_str(unsigned char* str)								// 문자열 송신
{
    while(*str){
        USART0_tx(*str++);
    }
}

int USART_Available(void)										// 수신된 데이터가 있는지 확인
{
    return (UCSR0A & (1 << RXC0));								
}

// ---------------------------- 기타 함수 ----------------------------
void Port_Init(void)											// 포트 설정
{	
    DDRC = 0xFF;												// PORTC 출력으로 설정
    DDRB = 0x0F;												// PORTB 하위 4비트 (0~3) 출력으로 설정
}

unsigned int read_adc(void)										// ADC값 읽는 함수									
{
    ADCSRA |= (1 << ADSC);
    while (!(ADCSRA & (1 << ADIF)));
    ADCSRA |= (1 << ADIF);
    return ADC;
}

void keypad_Init()												// 키패드 초기화
{
    DDRD = 0xF0;												// 하위 4비트(0~3)는 입력(행), 상위 4비트(4~7)는 출력(열)
}

char keypad_getkey()											// 키패드 입력 함수
{
    PORTD = 0x10;
    _delay_us(5);
    if ((PIND & 0x01) == 0x01) return '1';
    if ((PIND & 0x02) == 0x02) return '4';
    if ((PIND & 0x04) == 0x04) return '7';
    if ((PIND & 0x08) == 0x08) return '*';

    PORTD = 0x20;
    _delay_us(5);
    if ((PIND & 0x01) == 0x01) return '2';
    if ((PIND & 0x02) == 0x02) return '5';
    if ((PIND & 0x04) == 0x04) return '8';
    if ((PIND & 0x08) == 0x08) return '0';

    PORTD = 0x40;
    _delay_us(5);
    if ((PIND & 0x01) == 0x01) return '3';
    if ((PIND & 0x02) == 0x02) return '6';
    if ((PIND & 0x04) == 0x04) return '9';
    if ((PIND & 0x08) == 0x08) return '#';

    return 0;
}

void clear_input(void)											// 입력된 비밀번호 초기화 함수
{
    for (int i = 0; i < PASSWORD_LENGTH; i++)
        input_pass[i] = 0;
    input_index = 0;
}

void motor_Init(void)
{
    DDRG |= 0x0F;                                               // PORTG 하위 4비트 출력 설정 (모터 제어용)
    PORTG &= ~0x0F;                                             // 모터 초기 출력 모두 끔
}

void StepMotor(void)											// 스탭모터 구동(시계방향 1바퀴)
{
    int i = 0;
    for(i=0; i<12; i++)
    {
        PORTG = 0x08;
        _delay_ms(10);
        PORTG = 0x04;
        _delay_ms(10);
        PORTG = 0x02;
        _delay_ms(10);
        PORTG = 0x01;
        _delay_ms(10);
    }
}

void motor_stop(void)											// 모터멈춤
{
    PORTG &= 0xF0;                                              // 하위 4비트 모두 0으로 만들어 모터 전원 차단
}

// ---------------------------- 메인 함수 ----------------------------
int main(void)
{
    DDRF = 0x00;                                                // ADC 입력
    DDRA = 0xFF;                                                // LED 출력
    PORTA = 0x00;                                               // 초기 LED off

    ADMUX = 0x00;                                               // ADC 채널 0
    ADCSRA = 0x87;                                              // ADC 활성화, 분주비 128

    Port_Init();
    LCD_Init();
    keypad_Init();
    Init_USART();		
    motor_Init();									

    // Timer 설정
    ASSR = 0x00;                                                // 내부 클럭 사용
    TIMSK = 0x04;                                               // 타이머/카운터 1 인터럽트 사용
    TCCR1A = 0x00;
    TCCR1B = 0x05;                                              // 분주비 1024
    TCNT1H = 0xf4;                                              // 타이머 초기값 상위 8비트
    TCNT1L = 0xc0;                                              // 타이머 초기값 하위 8비트

    sei();                                                      // 전역 인터럽트 허용

    char Cds[16];												// 가변저항 값을 문자열로 저장하기 위한 배열
	
    LCD_Clear();
    LCD_pos(0, 0);
    LCD_STR("First Password");                                  // 맨 처음 LCD 출력 메시지

    while (1)
    {
		unsigned int adc_value = read_adc();                    // ADC값 읽기
		
        if (!adc_max) {											// ADC값이 최대가 아니면
            sprintf(Cds,"%4d", read_adc());						// 가변저항값을 출력
            LCD_pos(1, 0);
            LCD_STR(Cds);
            _delay_ms(200);
        }
        

        if (adc_value >= 1020)									// 처음 최대값에 도달하면
        {
            if (!adc_max)									    // 중복 실행 방지(처음 한번만 실행)
            {
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Access!!!!");
			
                PORTA |= 0x01;                                  // 1번 LED ON
                PORTA &= ~ 0x02;                                // 2번 LED OFF
                _delay_ms(2000);

                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Second Password");

                clear_input();                                  // 비밀번호 입력 초기화
                m_cnt = 0;
                t_cnt = 0;
				sec = 0;
                timeout = 0;
				
                TIMSK |= (1 << TOIE1);                          // 타이머 인터럽트 활성화
                timer_start = 1;

                adc_max = 1;							        // ADC 잠금 해제
                password_stage = 1;                             // 비밀번호 입력 상태
                first_lock_open = 1;                            // 1번째 잠금 해제 상태 설정
            }

            if (timeout)                                        // 타임아웃이 발생되면
            {
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("TIMEOUT!!!!!!!");
			
                for (int i = 0; i < 20; i++)                    // 1,2번 LED가 20번 켜졌다 꺼짐
                {
                    PORTA |= 0x01;                              // 1번 LED ON
                    _delay_ms(50);
                    PORTA &= ~0x01;                             // 1번 LED OFF
                    PORTA |= 0x02;                              // 2번 LED ON
                    _delay_ms(50);
                    PORTA &= ~0x02;                             // 2번 LED OFF
                }
			
                PORTA |= 0x01;									// 타임아웃 후 다시 1번 LED만 점등
                PORTA &= ~0x02;									// 아직 2단계 비밀번호 미입력
			
                clear_input();									// 비밀번호 초기화
                password_stage = 1;								// 다시 비밀번호 입력
                timer_start = 1;								// 타이머카운터 다시 시작
                timeout = 0;									// 타임아웃 초기화
                m_cnt = 0;
                t_cnt = 0;
				sec = 0;
				
                TIMSK |= (1 << TOIE1);                          // 타이머 다시 켜기
			
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Second Password");
                continue;
            }
		
            if (password_stage == 1)                            // 비밀번호 입력 단계일 때
            {
				char time_str[16];
				sprintf(time_str, "%2d s", sec);
				LCD_pos(1, 11);									// 2번째 줄, 11번째 칸 위치 
				LCD_STR(time_str);
				
                char key = keypad_getkey();						// 키패드에서 입력된 키 값을 key에 저장
                if (key != 0)                                   // 키가 눌리면
                {
                    if (input_index < PASSWORD_LENGTH && ((key >= '0' && key <= '9') || key == '*' || key == '#'))
                    {
                        input_pass[input_index] = key;          // 입력된 값을 저장
                        LCD_pos(1, input_index);                // 두번째 줄, 입력위치로 커서이동
                        LCD_CHAR(key);                          // 입력한 문자 LCD에 표시
                        input_index++;							// 키 개수 증가
                        _delay_ms(300);							// 입력딜레이
                    }

                    if (input_index == PASSWORD_LENGTH)         // 비밀번호 입력 완료 시
                    {
                        int correct = 1;						// 비밀번호가 맞다고 가정
                        for (int i = 0; i < PASSWORD_LENGTH; i++)
                        {
                            if (input_pass[i] != password[i])   // 비밀번호가 일치하는지 아닌지 확인
                            {
                                correct = 0;					// 값이 하나라도 다르면 틀림
                                break;
                            }
                        }

                        LCD_Clear();

                        if (correct)                            // 비밀번호가 맞으면
                        {
                            LCD_pos(0, 0);
                            LCD_STR("Access!!!!");
                            PORTA |= 0x02;                      // 2번 LED ON
                            _delay_ms(2000);

                            password_stage = 0;                 // 비밀번호 단계 초기화
							sec = 0;							// 초 경과 초기화
                            timer_start = 0;                    // 타이머 시작 초기화
                            TIMSK &= ~(1 << TOIE1);             // 타이머 인터럽트 끄기
                            second_lock_open = 1;               // 2번째 잠금 해제 상태 설정

                            // 타이머 변수 초기화
                            m_cnt = 0;
                            t_cnt = 0;
                            timeout = 0;
						
                            LCD_Clear();
                            LCD_pos(0, 0);
                            LCD_STR("Final Password");
                            LCD_pos(1, 0);
                            LCD_STR("Type on the key");
                        }
                        else                                    // 비밀번호가 틀리면
                        {
                            for (int i = 0; i < 5; i++)
                            {
                                LCD_pos(0, 0);
                                LCD_STR("Fail!!!!");
                                PORTA ^= 0x02;                  // 2번 LED와 LCD화면 5번 깜빡임
                                _delay_ms(300);
                                LCD_Clear();
                                PORTA ^= 0x02;
                                _delay_ms(300);
                            }
                            PORTA &= ~ 0x02;                    // 2번 LED OFF

                            LCD_pos(0, 0);
                            LCD_STR("Second Password");
                            clear_input();                      // 입력된 비밀번호 초기화
                            input_index = 0;					// 입력 인덱스 초기화(처음부터 다시 입력)
						    // 타이머 변수 초기화
                            m_cnt = 0;
                            t_cnt = 0;
                            timeout = 0;
							sec = 0;
                            TIMSK |= (1 << TOIE1);              // 타이머 인터럽트 활성화
                        }
                    }
                }
            }
        }
        else                                                    // ADC가 최대값이 아니면
        {
            if (adc_max)									    // 이전에 최대값이였으면 초기화
            {
                _delay_ms(500);
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("First Password");                      // 다시 첫 번째 단계로
			    clear_input();
				
                adc_max = 0;									// ADC 최대값 초기화
                PORTA &= ~( 0x01 | 0x02 );                      // 1,2번 LED OFF (3번은 유지)
				
                password_stage = 0;								// 비밀번호 입력 단계 초기화
                timer_start = 0;								// 타이머 시작 초기화
                TIMSK &= ~(1 << TOIE1);                         // 타이머 인터럽트 비활성화
			
                // 잠금 상태 초기화
                first_lock_open = 0;                            // 1번째 잠금 다시 잠김
                second_lock_open = 0;                           // 2번째 잠금 다시 잠김
			
                PORTA &= ~0x04;                                 // 3번 LED OFF
                third_lock_open = 0;                            // 3단계 상태도 초기화
            }
        }
	
        // 시리얼 통신 확인 - 1,2번째 잠금이 모두 해제된 경우에만 동작
        // (1,2 잠금이 완료된 후에만 3번째 비밀번호 잠금해제 가능)
        if (first_lock_open && second_lock_open && USART_Available())
        {										
																// 1,2 모두 잠금 해제 상태이고, 수신데이터가 있는지 확인
            unsigned char ch = USART0_rx();						// 수신된 문자 읽어 ch에 저장
            USART0_tx(ch);										// 받은 문자 다시 송신(에코)
				
            if (ch == Final_pass)								// 문자 비교(읽은 문자와 저장된 비밀번호 값이 같은지)
            {
                PORTA |= 0x04;                                  // 3번 LED ON
                third_lock_open = 1;							// 3번 잠금 해제

                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Access!!!!");
		
                USART0_str((unsigned char*)"\r\nClear!!!!\r\n");// 시리얼통신으로 문자열 전송(성공)
                _delay_ms(2000);

                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Step Motor ON!");
                _delay_ms(1000);
		
                if (third_lock_open)                            // 3단계 잠금 해제되면
                {
                    int current_adc = 0;						// 현재 ADC값	
                    while (1) 
                    {
                        int current_adc = read_adc();			// 가변저항 값을 읽어 현재 ADC값에 저장
                        if (current_adc < 1020)                 // ADC값 변화하면 1단계 인증 해제이므로 정지
                        {										
                            break;
                        }
                        StepMotor();							// 그대로이면 모터구동
                        LCD_pos(1, 0);
                        LCD_STR("Running!!!!");
                        _delay_ms(1);
                    }
                    motor_stop();								// ADC값 변화하면 모터 정지
					
					// 상태 초기화
                    first_lock_open = 0;						// 첫번째 잠금상태 초기화
                    second_lock_open = 0;						// 두번째 잠금상태 초기화
                    third_lock_open = 0;						// 세번째 잠금상태 초기화
                    adc_max = 0;								// 가변저항 값 초기화
                    password_stage = 0;							// 비밀번호 입력 단계 초기화
                    timer_start = 0;							// 타이머 시작 초기화
                    timeout = 0;								// 타임아웃 초기화
					sec = 0;
			
                    while(USART_Available())
                    {
                        USART0_rx();                            // 모든 남은 데이터 버리기
                    }
			
                    LCD_Clear();
                    LCD_pos(0, 0);
                    LCD_STR("Step Motor OFF!");
			
                    PORTA &= ~ 0x01;                            // 1번 LED OFF
                    PORTA &= ~ 0x02;                            // 2번 LED OFF
                    PORTA &= ~ 0x04;                            // 3번 LED OFF
			
                    _delay_ms(2000);
			
                    LCD_Clear();
                    LCD_pos(0, 0);
                    LCD_STR("First Password");
			
                    clear_input();								// 키패드 입력 초기화
                    input_index = 0;							// 입력 인덱스 초기화
                    prev_adc = current_adc;						// 마지막 ADC값 저장
                }	
            }	
            else
            {
                while(USART_Available())
                {
                    USART0_rx();                                // 모든 남은 데이터 버리기
                }
                USART0_str((unsigned char*)"\r\nEnter password again!!!!\r\n");
																// 시리얼통신으로 문자열 전송(비밀번호 다시입력)
                for (int i = 0; i < 5; i++)
                {
                    LCD_pos(0, 0);
                    LCD_STR("Fail!!!!");
                    PORTA ^= 0x04;								// 3번 LED와 LCD화면 5번 깜빡임
                    _delay_ms(300);
                    LCD_Clear();
                    PORTA ^= 0x04;
                    _delay_ms(300);
                }
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Final Password");
                LCD_pos(1, 0);
                LCD_STR("Type on the key");						// 마지막 비밀번호 입력 안내
            }
        }
        else if (USART_Available() && (!first_lock_open || !second_lock_open))
																// 입력된 데이터가 있고 1단계나 2단계 인증이 아직 미완료
        {
            unsigned char ch = USART0_rx();						// 수신된 문자 읽어 ch에 저장
            USART0_tx(ch);										// 받은 문자 다시 송신(에코)
           
            while(USART_Available()) {							// 1,2번째 잠금이 해제되지 않은 상태에서 시리얼 입력이 있으면 무시하고 메시지 출력
                USART0_rx();                                    // 모든 대기중인 데이터를 읽어서 버퍼를 완전히 비움
            }
            USART0_str((unsigned char*)"\r\nComplete steps 1 and 2 first!\r\n");
																// 시리얼통신으로 문자열 전송(1단계와 2단계 먼저 완료해라)
            LCD_Clear();
            LCD_pos(0, 0);
            LCD_STR("No Access!!");
	
            _delay_ms(3000);
			
            // 상태 초기화
			clear_input();
            adc_max = 0;
            first_lock_open = 0;
            second_lock_open = 0;
            third_lock_open = 0;
            input_index = 0;
            timer_start = 0;
            timeout = 0;
			sec = 0;

            TIMSK &= ~(1 << TOIE1);                             // 타이머 끄기

            PORTA &= ~(0x07);                                   // LED 모두 끄기 (1,2,3번 LED 모두 OFF)

            LCD_Clear();
            LCD_pos(0, 0);
            LCD_STR("First Password");                          // 다시 첫 단계로
	
            _delay_ms(2000);
        }
        _delay_ms(50);
    }
    return 0;
}