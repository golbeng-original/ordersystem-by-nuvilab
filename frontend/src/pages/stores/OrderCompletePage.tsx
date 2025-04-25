import { Box, Button, List, ListItem, ListItemText, Typography } from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";

interface OrderItem {
    id: number;
    title: string;
    quantity: number;
    price: number;
}

interface ReceiveAddress {
    ownerName: string;
    address: string;
    phoneNumber: string;
}

const orderItems: OrderItem[] = [
    { id: 1, title: "Menu 1", quantity: 2, price: 1000 },
    { id: 2, title: "Menu 2", quantity: 1, price: 2000 },
    { id: 3, title: "Menu 3", quantity: 3, price: 3000 },
]

const receiveAddress: ReceiveAddress = {
    ownerName: "홍길동",
    address: "서울시 강남구 역삼동",
    phoneNumber: "010-1234-5678",
}

const OrderCompletePage = () => {

    const { orderId } = useParams();

    console.log(orderId);

    const navigate = useNavigate();

    const onHandleGoHome = () => {
      navigate('/stores'); // 홈으로 이동
    };

    const onHandleMyOrder = () => {
        navigate('/user/orders'); // 홈으로 이동
    };

    return (
        <Box sx={{ 
            background: 'linear-gradient(to right, #667eea, #764ba2)',
            minWidth: '100vh',
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexDirection: 'column',
        }}>
            <Typography variant="h4" gutterBottom>
                🎉 주문이 완료되었습니다!
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
                주문해 주셔서 감사합니다. 아래는 주문 내역입니다.
            </Typography>
            <List sx={{ 
                textAlign: 'left', 
                mb: 3, 
            }}>
                {orderItems.map((item, index) => (
                    <ListItem key={index} disableGutters>
                        <Box sx={{
                            display: 'flex', 
                            justifyContent: 'space-between', 
                            width: '100%', 
                        }}>
                            <Typography >
                                {`${item.title} x ${item.quantity}개`}
                            </Typography>
                            <Typography>
                                {`금액: ${item.price.toLocaleString()}원`}
                            </Typography>
                        </Box>
                    </ListItem>
                ))}
            </List>

            <Typography variant="h6" gutterBottom>
                배송지 정보
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
                {`받는 분: ${receiveAddress.ownerName}`}
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
                {`주소 : ${receiveAddress.address}`}
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
                {`전화번호 : ${receiveAddress.phoneNumber}`}
            </Typography>

            <Box sx={{
                display: 'flex', 
                justifyContent: 'center', 
                width: '100%', 
                mt: 3,
            }}>
                <Button variant="contained" color="primary" onClick={onHandleGoHome}>
                    홈으로 돌아가기
                </Button>
                <Box sx={{ ml: 2 }} />
                <Button variant="contained" color="primary" onClick={onHandleMyOrder}>
                    내 주문 목록
                </Button>
            </Box>

      </Box>
    );
}

export default OrderCompletePage;