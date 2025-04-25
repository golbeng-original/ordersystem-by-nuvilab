import { Box, Button, List, ListItem, Typography } from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import useGetOrder, { OrderPayload } from "../../features/stores/hooks/useGetOrder";
import { useEffect } from "react";


const OrderedItems = (props:{
    orderPayload: OrderPayload | null;
}) => {

    const { orderPayload } = props;

    if( !orderPayload ) {
        return (
            <></>
        )
    }

    return (
        <List sx={{ 
            textAlign: 'left', 
            mb: 3, 
        }}>
            {Array.from(orderPayload.orderedItems).map((item, index) => (
                <ListItem key={index} disableGutters>
                    <Box sx={{
                        display: 'flex', 
                        justifyContent: 'space-between', 
                        width: '100%',
                    }}>
                        <Typography >
                            {`${item.menuName} x ${item.quantity}개`}
                        </Typography>
                        <Box sx={{width: '30px'}}/>
                        <Typography>
                            {`금액: ${item.unitPrice.toLocaleString()}원`}
                        </Typography>
                    </Box>
                </ListItem>
            ))}
        </List>
    );

}

const ReceiveAddressWidget = (props:{
    orderPayload: OrderPayload | null;
}) => {
    const { orderPayload } = props;

    if( !orderPayload ) {
        return (
            <></>
        );
    }

    return (
        <>
            <Typography variant="body1" sx={{ mb: 1 }}>
                {`받는 분: ${orderPayload.deliveryInfo.receiverName}`}
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
                {`주소 : ${orderPayload.deliveryInfo.address}`}
            </Typography>
            <Typography variant="body1" sx={{ mb: 1 }}>
                {`전화번호 : ${orderPayload.deliveryInfo.receiverPhoneNumber}`}
            </Typography>
        </>
    );
}

const OrderCompletePage = () => {

    const { orderId } = useParams();

    const navigate = useNavigate();

    const {orderPayload, requestOrderInfo} = useGetOrder();

    useEffect(() => {
        requestOrderInfo(orderId!);
    }, []);

    const onHandleGoHome = () => {
      navigate('/stores'); // 홈으로 이동
    };

    const onHandleMyOrder = () => {
        navigate('/user/orders'); // 홈으로 이동
    };

    return (
        <Box sx={{ 
            background: 'linear-gradient(to right, #667eea, #764ba2)',
            width: '100vw',
            height: '100vh',
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
            <OrderedItems orderPayload={orderPayload} />

            <Typography variant="h6" gutterBottom>
                배송지 정보
            </Typography>
            <ReceiveAddressWidget orderPayload={orderPayload} />

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