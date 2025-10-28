/*
 * FaceDetector.c
 *
 * Created: 2025-10-27 오후 4:26:38
 * Author : COMPUTER
 *
 * 시리얼 통신으로 '1'을 받으면 LCD에 비밀번호 입력 화면 출력
 * 키패드로 4자리 비밀번호를 입력받아 확인
 */ 

#define F_CPU 14745600UL
#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <stdio.h>

// LCD 핀 정의
#define LCD_WDATA	PORTC
#define LCD_WINST	PORTC
#define LCD_CTRL	PORTB
#define LCD_RS		0
#define LCD_RW		1
#define LCD_EN		2

#define PASSWORD_LENGTH 4

// 함수 선언
void LCD_Data(char ch);
void LCD_Comm(char ch);
void LCD_CHAR(char c);
void LCD_STR(char *str);
void LCD_pos(char col, char row);
void LCD_Clear(void);
void LCD_Init(void);

void Init_USART(void);
unsigned char USART0_rx(void);
int USART_Available(void);

void keypad_Init(void);
char keypad_getkey(void);

// 전역 변수
char password[PASSWORD_LENGTH] = {'1', '2', '3', '4'};  // 비밀번호 설정
char input_pass[PASSWORD_LENGTH];                        // 입력받은 비밀번호
int input_index = 0;                                     // 입력 인덱스

// ---------------------------- LCD 함수 ----------------------------
void LCD_Data(char ch)
{
    LCD_CTRL |= (1 << LCD_RS);
    LCD_CTRL &= ~(1 << LCD_RW);
    LCD_CTRL |= (1 << LCD_EN);
    _delay_us(50);
    LCD_WDATA = ch;
    _delay_us(50);
    LCD_CTRL &= ~(1 << LCD_EN);
}

void LCD_Comm(char ch)
{
    LCD_CTRL &= ~(1 << LCD_RS);
    LCD_CTRL &= ~(1 << LCD_RW);
    LCD_CTRL |= (1 << LCD_EN);
    _delay_us(50);
    LCD_WINST = ch;
    _delay_us(50);
    LCD_CTRL &= ~(1 << LCD_EN);
}

void LCD_CHAR(char c)
{
    LCD_Data(c);
    _delay_ms(1);
}

void LCD_STR(char *str)
{
    while (*str != 0)
    {
        LCD_CHAR(*str);
        str++;
    }
}

void LCD_pos(char col, char row)
{
    LCD_Comm(0x80 | (row + col * 0x40));
}

void LCD_Clear(void)
{
    LCD_Comm(0x01);
    _delay_ms(2);
}

void LCD_Init(void)
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
void Init_USART()
{
    UCSR0A = 0x00;
    UCSR0B = (1 << RXEN0) | (1 << TXEN0);
    UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
    UBRR0H = 0;
    UBRR0L = 7;  // 115200bps
}

unsigned char USART0_rx()
{
    while(!(UCSR0A & (1 << RXC0)));
    return UDR0;
}

int USART_Available(void)
{
    return (UCSR0A & (1 << RXC0));
}

// ---------------------------- 키패드 함수 ----------------------------
void keypad_Init()
{
    DDRD = 0xF0;  // 하위 4비트(0~3)는 입력(행), 상위 4비트(4~7)는 출력(열)
}

char keypad_getkey()
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

// ---------------------------- 메인 함수 ----------------------------
int main(void)
{
    // 포트 초기화
    DDRC = 0xFF;  // LCD 데이터 출력
    DDRB = 0x0F;  // LCD 제어 출력
    DDRA = 0xFF;  // LED 출력
    PORTA = 0x00; // 초기 LED OFF
    
    LCD_Init();
    keypad_Init();
    Init_USART();
    
    LCD_Clear();
    LCD_pos(0, 0);
    LCD_STR("Waiting...");
    
    int password_mode = 0;  // 0: 대기, 1: 비밀번호 입력 모드
    
    while(1)
    {
        // 시리얼 통신 확인
        if(USART_Available())
        {
            unsigned char ch = USART0_rx();
            
            if(ch == '1')   // '1' 받으면 비밀번호 입력 모드
            {
                password_mode = 1;
                input_index = 0;
                
                // 입력 초기화
                for(int i = 0; i < PASSWORD_LENGTH; i++)
                    input_pass[i] = 0;
                
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Enter Password");
                LCD_pos(1, 0);
                LCD_STR("    ");  // 4자리 공백
                LCD_pos(1, 0);    // 커서를 입력 시작 위치로
                
                PORTA |= 0x01;    // LED ON (비밀번호 입력 모드 표시)
            }
            else if(ch == '0')  // '0' 받으면 대기 모드
            {
                password_mode = 0;
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Not Access");
                
                // 모든 LED 5번 깜빡임
                for(int i = 0; i < 5; i++)
                {
                    PORTA = 0xFF;  // 모든 LED ON
                    _delay_ms(300);
                    PORTA = 0x00;  // 모든 LED OFF
                    _delay_ms(300);
                }
                
                _delay_ms(1000);
                
                LCD_Clear();
                LCD_pos(0, 0);
                LCD_STR("Waiting...");
            }
        }
        
        // 비밀번호 입력 모드일 때 키패드 처리
        if(password_mode)
        {
            char key = keypad_getkey();
            
            if(key != 0)  // 키가 눌렸으면
            {
                if(input_index < PASSWORD_LENGTH && 
                   ((key >= '0' && key <= '9') || key == '*' || key == '#'))
                {
                    input_pass[input_index] = key;
                    LCD_pos(1, input_index);
                    LCD_CHAR('*');  // 보안을 위해 '*'로 표시
                    input_index++;
                    _delay_ms(300);
                }
                
                // 4자리 모두 입력되면 확인
                if(input_index == PASSWORD_LENGTH)
                {
                    int correct = 1;
                    for(int i = 0; i < PASSWORD_LENGTH; i++)
                    {
                        if(input_pass[i] != password[i])
                        {
                            correct = 0;
                            break;
                        }
                    }
                    
                    LCD_Clear();
                    
                    if(correct)  // 비밀번호 일치
                    {
                        LCD_pos(0, 0);
                        LCD_STR("Access!!");
                        PORTA = 0xFF;  // 모든 LED ON (계속 켜짐)
                        
                        _delay_ms(2000);
                        
                        // 비밀번호 입력 모드 종료 (LED는 켜진 상태 유지)
                        password_mode = 0;
                        
                        LCD_Clear();
                        LCD_pos(0, 0);
                        LCD_STR("Management Mode");
                        continue;  // 아래 LED OFF 코드 건너뛰기
                    }
                    else  // 비밀번호 불일치
                    {
                        LCD_pos(0, 0);
                        LCD_STR("Wrong Password!!");
                        
                        // 모든 LED 5번 깜빡임
                        for(int i = 0; i < 5; i++)
                        {
                            PORTA = 0xFF;  // 모든 LED ON
                            _delay_ms(300);
                            PORTA = 0x00;  // 모든 LED OFF
                            _delay_ms(300);
                        }
                        PORTA |= 0x01;  // 입력 모드 LED만 다시 켜기
                        
                        _delay_ms(1000);
                        
                        // 다시 입력 화면으로
                        input_index = 0;
                        for(int i = 0; i < PASSWORD_LENGTH; i++)
                            input_pass[i] = 0;
                        
                        LCD_Clear();
                        LCD_pos(0, 0);
                        LCD_STR("Enter Password");
                        LCD_pos(1, 0);
                        LCD_STR("    ");
                        LCD_pos(1, 0);
                        continue;
                    }
                }
            }
        }
        _delay_ms(50);
    }
    return 0;
}
