import moment from "moment";
import { Box, Button, Divider, List, ListItem, ListItemText, Paper, Typography } from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";

interface OrderedMenuItem {
    id: string;
    name: string;
    price: number;
    quantity: number;
}

interface Ordered {
    id: string;
    orderDate:moment.Moment;
    orderdMenuItems: OrderedMenuItem[];

}

const orders: Ordered[] = [
    {
        id: "1",
        orderDate: moment("2023-10-01"),
        orderdMenuItems: [
            { id: "1", name: "Menu 1", price: 1000, quantity: 2 },
            { id: "2", name: "Menu 2", price: 2000, quantity: 1 },
        ],
    },
    {
        id: "2",
        orderDate: moment("2023-10-02"),
        orderdMenuItems: [
            { id: "3", name: "Menu 3", price: 3000, quantity: 3 },
        ],
    }
]

const MyOrderListBody = (props:{
    orders: Ordered[];
}) => {

    const {orders} = props;

    if( orders.length === 0) {
        return (
            <Box>
                <Typography variant="body1">주문 내역이 없습니다.</Typography>
            </Box>
        )
    }

    return (
        <List>
            {orders.map((order) => (
                <Paper key={order.id} sx={{ mb: 2, p: 2 }} elevation={2}>
                    <Typography variant="h6" sx={{ mb: 1 }}>
                        주문일: {order.orderDate.format("YYYY-MM-DD hh:mm")}
                    </Typography>

                    {order.orderdMenuItems.map((item, idx) => (
                        <ListItem key={idx} disableGutters>
                            <ListItemText
                                primary={`${item.name} x ${item.quantity}개`}
                                secondary={`단가: ${item.price.toLocaleString()}원`}
                            />
                        </ListItem>
                    ))}
                </Paper>
            ))}
        </List>
    );
}

const MyOrdreListPage = () => {

    const navigate = useNavigate();

    const onHandleGoHome = () => {
        navigate('/stores');
    }

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
                🧾 내 주문 목록
            </Typography>
    
            <MyOrderListBody orders={orders}/>

            <Button variant="contained" color="primary" onClick={onHandleGoHome}>
                홈으로 돌아가기
            </Button>
        </Box>
      );
}

export default MyOrdreListPage;